package com.iudigital.radio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.iudigital.radio.hardware.VibrationHelper
import com.iudigital.radio.media.RadioPlayerManager
import com.iudigital.radio.ui.RadioAppScreen
import com.iudigital.radio.ui.theme.IUDigitalRadioTheme

/**
 * Punto de entrada principal de la aplicación IU Digital Radio.
 * Asume el rol de desarrollador Full-Stack Android (Opción B - Trabajo Individual).
 */
class MainActivity : ComponentActivity() {

    private lateinit var playerManager: RadioPlayerManager
    private lateinit var vibrationHelper: VibrationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar cargador de imágenes con caché en memoria y disco
        val imageLoader = ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(20 * 1024 * 1024)
                    .build()
            }
            .crossfade(true)
            .allowHardware(false) // Mejor compatibilidad con dispositivos Xiaomi/Redmi
            .error(android.R.drawable.ic_menu_gallery)
            .build()
        Coil.setImageLoader(imageLoader)

        // Inicializar gestores de hardware y multimedia
        playerManager = RadioPlayerManager(this)
        vibrationHelper = VibrationHelper(this)

        setContent {
            IUDigitalRadioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RadioAppScreen(
                        playerManager = playerManager,
                        vibrationHelper = vibrationHelper
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos de ExoPlayer para evitar memory leaks
        playerManager.release()
    }
}
