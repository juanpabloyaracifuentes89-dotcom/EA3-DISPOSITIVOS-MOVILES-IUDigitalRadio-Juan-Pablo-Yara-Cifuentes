package com.iudigital.radio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iudigital.radio.model.RadioStation
import com.iudigital.radio.ui.theme.ActiveGreen
import com.iudigital.radio.ui.theme.IUDigitalDarkNavy
import com.iudigital.radio.ui.theme.IUDigitalNavy
import com.iudigital.radio.ui.theme.IUDigitalRed
import com.iudigital.radio.ui.theme.IUDigitalYellow

/**
 * RF-04, RF-05, RF-07: Sección Central de Reproducción Multimedia Compacta.
 * Estilizada con el Azul Marino, Rojo y Amarillo Institucional de IU Digital de Antioquia.
 */
@Composable
fun PlayerCardSection(
    currentStation: RadioStation,
    isPlaying: Boolean,
    isMuted: Boolean,
    isBuffering: Boolean,
    onPlayPauseClick: () -> Unit,
    onMuteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animación continua de rotación estilo CD en marcha cuando está reproduciendo
    val infiniteTransition = rememberInfiniteTransition(label = "cd_rotation")
    val cdRotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            IUDigitalNavy,
                            IUDigitalDarkNavy
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header compacto: Badge "EN VIVO" e indicador de frecuencia
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = if (isPlaying) ActiveGreen else Color.Gray,
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBuffering) "CONECTANDO..." else if (isPlaying) "EN VIVO" else "PAUSA",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        color = IUDigitalYellow,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = currentStation.frequency,
                            color = IUDigitalNavy,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // FOTO / LOGO DE EMISORA EN DISCO CD GIRATORIO CON ARO AMARILLO INSTITUCIONAL
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .rotate(if (isPlaying) cdRotationAngle else 0f)
                        .clip(CircleShape)
                        .background(Color(0xFF111827))
                        .border(2.5.dp, IUDigitalYellow, CircleShape)
                        .border(5.dp, Color(0xFF1F2937), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    StationAvatar(
                        station = currentStation,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Agujero/Aro Central del Disco CD
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                            .border(2.dp, IUDigitalNavy, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Nombre y Género de la Emisora
                Text(
                    text = currentStation.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = currentStation.genre,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Botones de Control Compactos (Mute & Play/Pause)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = onMuteClick,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isMuted) IUDigitalRed else Color.White.copy(alpha = 0.25f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Mute",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Button(
                        onClick = onPlayPauseClick,
                        modifier = Modifier.size(54.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IUDigitalRed,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        if (isBuffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                                modifier = Modifier.size(30.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
