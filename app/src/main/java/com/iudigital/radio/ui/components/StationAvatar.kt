package com.iudigital.radio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.iudigital.radio.model.RadioStation
import java.util.Locale

/**
 * Componente de avatar/logo inteligente y súper fluido para las emisoras de radio.
 * Garantiza 0 milisegundos de espera mostrando una insignia HD de alta calidad
 * mientras se carga la imagen remota o si la URL de la emisora falla.
 */
@Composable
fun StationAvatar(
    station: RadioStation,
    modifier: Modifier = Modifier
) {
    val (gradientColors, badgeText) = getStationBrandStyle(station.name)

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(brush = Brush.linearGradient(colors = gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        if (!station.iconUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                    .data(station.iconUrl)
                    .crossfade(true)
                    .allowHardware(false)
                    .build(),
                contentDescription = station.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    StationBadgeFallback(badgeText = badgeText)
                },
                error = {
                    android.util.Log.w("StationAvatar", "Error cargando logo: ${station.iconUrl}")
                    StationBadgeFallback(badgeText = badgeText)
                }
            )
        } else {
            StationBadgeFallback(badgeText = badgeText)
        }
    }
}

@Composable
private fun StationBadgeFallback(badgeText: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (badgeText.length <= 4) {
            Text(
                text = badgeText,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(2.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Radio,
                contentDescription = "Radio",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxSize(0.55f)
            )
        }
    }
}

private fun getStationBrandStyle(stationName: String): Pair<List<Color>, String> {
    val nameLower = stationName.lowercase(Locale.getDefault())

    return when {
        nameLower.contains("blu") -> listOf(Color(0xFF0052D4), Color(0xFF4364F7)) to "BLU"
        nameLower.contains("caracol") -> listOf(Color(0xFFD32F2F), Color(0xFF1976D2)) to "CRC"
        nameLower.contains("w radio") || nameLower.contains("wradio") -> listOf(Color(0xFFB71C1C), Color(0xFF212121)) to "W"
        nameLower.contains("olimpica") || nameLower.contains("olímpica") -> listOf(Color(0xFFE53935), Color(0xFFFBC02D)) to "OLI"
        nameLower.contains("tropicana") -> listOf(Color(0xFFFF8F00), Color(0xFFFF3D00)) to "TROP"
        nameLower.contains("mega") -> listOf(Color(0xFF8E24AA), Color(0xFFD81B60)) to "MEGA"
        nameLower.contains("nacional") -> listOf(Color(0xFF2E7D32), Color(0xFFFBC02D)) to "RNC"
        nameLower.contains("radionica") || nameLower.contains("radiónica") -> listOf(Color(0xFF00838F), Color(0xFFAD1457)) to "RAD"
        nameLower.contains("rcn") -> listOf(Color(0xFF1565C0), Color(0xFFC62828)) to "RCN"
        nameLower.contains("vibra") -> listOf(Color(0xFFE91E63), Color(0xFF9C27B0)) to "VIBRA"
        nameLower.contains("besame") || nameLower.contains("bésame") -> listOf(Color(0xFFEC407A), Color(0xFFAB47BC)) to "BÉSAME"
        nameLower.contains("oxigeno") || nameLower.contains("oxígeno") -> listOf(Color(0xFF7CB342), Color(0xFF009688)) to "OXI"
        nameLower.contains("candela") -> listOf(Color(0xFFFF6D00), Color(0xFFD50000)) to "CAND"
        nameLower.contains("kalle") || nameLower.contains("la kalle") -> listOf(Color(0xFFFFAB00), Color(0xFFDD2C00)) to "KALLE"
        nameLower.contains("iu digital") || nameLower.contains("iud") -> listOf(Color(0xFF1B365D), Color(0xFFCE2029)) to "IUD"
        else -> {
            val words = stationName.trim().split(" ").filter { it.isNotBlank() }
            val initials = if (words.size >= 2) {
                "${words[0].first()}${words[1].first()}".uppercase(Locale.getDefault())
            } else if (words.isNotEmpty()) {
                words[0].take(3).uppercase(Locale.getDefault())
            } else {
                "RADIO"
            }
            listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6)) to initials
        }
    }
}
