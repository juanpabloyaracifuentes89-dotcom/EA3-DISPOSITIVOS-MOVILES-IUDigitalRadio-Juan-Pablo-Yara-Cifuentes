package com.iudigital.radio.ui

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iudigital.radio.hardware.VibrationHelper
import com.iudigital.radio.media.RadioPlayerManager
import com.iudigital.radio.model.RadioStation
import com.iudigital.radio.model.SampleRadioStations
import com.iudigital.radio.ui.components.PlayerCardSection
import com.iudigital.radio.ui.components.ProfileSection
import com.iudigital.radio.ui.components.StationCatalogSection

import com.iudigital.radio.api.RadioBrowserApi

/**
 * Saver personalizado para guardar y restaurar Bitmaps en rememberSaveable
 * ante cambios de configuración (rotación de pantalla).
 */
val BitmapSaver = Saver<Bitmap?, Any>(
    save = { bitmap -> bitmap },
    restore = { value -> value as? Bitmap }
)

/**
 * RF-01 & RF-04: Pantalla Principal Maquetada en Compose con Layout Adaptativo.
 *
 * - VERTICAL (Portrait): Column con ProfileSection + PlayerCard + StationCatalog apilados
 * - HORIZONTAL (Landscape): Row con PlayerCard+Profile a la izquierda y StationCatalog a la derecha
 *
 * Manejo de estado mutable y rememberSaveable para conservar foto, emisora activa
 * y estado de reproducción ante rotaciones del dispositivo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioAppScreen(
    playerManager: RadioPlayerManager,
    vibrationHelper: VibrationHelper,
    modifier: Modifier = Modifier
) {
    var stations by remember {
        mutableStateOf(SampleRadioStations.defaultStations)
    }

    var userPhoto by rememberSaveable(stateSaver = BitmapSaver) {
        mutableStateOf<Bitmap?>(null)
    }

    var selectedStation by rememberSaveable {
        mutableStateOf(stations.first())
    }

    var isPlaying by rememberSaveable {
        mutableStateOf(false)
    }

    var isMuted by rememberSaveable {
        mutableStateOf(false)
    }

    var isBuffering by remember {
        mutableStateOf(false)
    }

    // Cargar emisoras reales en vivo desde Radio-Browser API (https://www.radio-browser.info/)
    LaunchedEffect(Unit) {
        val liveStations = RadioBrowserApi.fetchColombianStations()
        if (liveStations.isNotEmpty()) {
            stations = liveStations
            // Solo seleccionar la primera si aún no se ha interactuado o si la seleccionada es la de muestra inicial
            val isDefault = SampleRadioStations.defaultStations.any { it.id == selectedStation.id }
            if (isDefault && !isPlaying) {
                selectedStation = liveStations.first()
            }
        }
    }

    // Escuchar actualizaciones de reproducción en tiempo real desde ExoPlayer
    DisposableEffect(playerManager) {
        playerManager.onPlaybackStateChanged = { playing, buffering ->
            isPlaying = playing
            isBuffering = buffering
        }
        onDispose {
            playerManager.onPlaybackStateChanged = null
        }
    }

    // Detectar orientación actual del dispositivo
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "IU ",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = com.iudigital.radio.ui.theme.IUDigitalRed
                            )
                            Text(
                                text = "DIGITAL RADIO",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = com.iudigital.radio.ui.theme.IUDigitalNavy
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(com.iudigital.radio.ui.theme.IUDigitalYellow)
                )
            }
        }
    ) { innerPadding ->
        if (isLandscape) {
            // ═══════════════════════════════════════════════════════════════
            // LAYOUT HORIZONTAL (Landscape): Reproductor izquierda | Lista derecha
            // ═══════════════════════════════════════════════════════════════
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // PANEL IZQUIERDO: Perfil + Reproductor (scrollable en caso de poco espacio vertical)
                Column(
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Perfil de usuario compacto
                    ProfileSection(
                        userPhoto = userPhoto,
                        onPhotoCaptured = { bitmap ->
                            userPhoto = bitmap
                        }
                    )

                    // Card del Reproductor
                    PlayerCardSection(
                        currentStation = selectedStation,
                        isPlaying = isPlaying,
                        isMuted = isMuted,
                        isBuffering = isBuffering,
                        onPlayPauseClick = {
                            vibrationHelper.vibrateClick()
                            if (isPlaying) {
                                playerManager.togglePlayPause()
                            } else {
                                playerManager.playStation(selectedStation)
                            }
                        },
                        onMuteClick = {
                            vibrationHelper.vibrateDouble()
                            isMuted = !isMuted
                            playerManager.setMuted(isMuted)
                        }
                    )
                }

                // PANEL DERECHO: Catálogo de Emisoras
                StationCatalogSection(
                    stations = stations,
                    selectedStation = selectedStation,
                    isPlaying = isPlaying,
                    onStationSelect = { station ->
                        vibrationHelper.vibrateClick()
                        selectedStation = station
                        playerManager.playStation(station)
                    },
                    modifier = Modifier
                        .weight(0.55f)
                        .fillMaxHeight()
                )
            }
        } else {
            // ═══════════════════════════════════════════════════════════════
            // LAYOUT VERTICAL (Portrait): Columna apilada como siempre
            // ═══════════════════════════════════════════════════════════════
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. SECCIÓN SUPERIOR: Perfil de usuario y disparo de Cámara (RF-02, RF-03)
                ProfileSection(
                    userPhoto = userPhoto,
                    onPhotoCaptured = { bitmap ->
                        userPhoto = bitmap
                    }
                )

                // 2. SECCIÓN CENTRAL: Card del Reproductor Interactivo (RF-04, RF-05, RF-07)
                PlayerCardSection(
                    currentStation = selectedStation,
                    isPlaying = isPlaying,
                    isMuted = isMuted,
                    isBuffering = isBuffering,
                    onPlayPauseClick = {
                        vibrationHelper.vibrateClick()
                        if (isPlaying) {
                            playerManager.togglePlayPause()
                        } else {
                            playerManager.playStation(selectedStation)
                        }
                    },
                    onMuteClick = {
                        vibrationHelper.vibrateDouble()
                        isMuted = !isMuted
                        playerManager.setMuted(isMuted)
                    }
                )

                // 3. SECCIÓN INFERIOR: Catálogo de Emisoras en LazyColumn (RF-06)
                StationCatalogSection(
                    stations = stations,
                    selectedStation = selectedStation,
                    isPlaying = isPlaying,
                    onStationSelect = { station ->
                        vibrationHelper.vibrateClick()
                        selectedStation = station
                        playerManager.playStation(station)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
