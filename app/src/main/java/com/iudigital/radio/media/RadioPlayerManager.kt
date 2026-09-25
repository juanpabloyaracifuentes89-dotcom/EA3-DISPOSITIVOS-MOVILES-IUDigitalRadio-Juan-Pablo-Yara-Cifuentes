package com.iudigital.radio.media

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.iudigital.radio.model.RadioStation

/**
 * RF-07: Reproducción Multimedia con Media3 ExoPlayer.
 * Gestor encargado de la inicialización, reproducción, pausa, mute
 * y liberación de recursos de audio en tiempo real.
 */
class RadioPlayerManager(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null
    
    // Callback para notificar cambios de estado a Jetpack Compose
    var onPlaybackStateChanged: ((isPlaying: Boolean, isBuffering: Boolean) -> Unit)? = null

    init {
        setupPlayer()
    }

    private fun setupPlayer() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        onPlaybackStateChanged?.invoke(isPlaying, false)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        val isBuffering = playbackState == Player.STATE_BUFFERING
                        val isPlaying = exoPlayer?.isPlaying == true
                        onPlaybackStateChanged?.invoke(isPlaying, isBuffering)
                    }
                })
            }
        }
    }

    /**
     * Carga y reproduce la emisora de radio especificada.
     */
    fun playStation(station: RadioStation) {
        exoPlayer?.let { player ->
            try {
                val mediaItem = MediaItem.fromUri(station.streamUrl)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = true
            } catch (e: Exception) {
                Log.e("RadioPlayerManager", "Error al reproducir ${station.name}: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Alterna entre Reproducción y Pausa.
     */
    fun togglePlayPause(): Boolean {
        return exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                false
            } else {
                if (player.mediaItemCount > 0) {
                    player.play()
                }
                true
            }
        } ?: false
    }

    /**
     * Alterna entre Mute (Silenciar) y Unmute (Activar Sonido).
     */
    fun setMuted(isMuted: Boolean) {
        exoPlayer?.volume = if (isMuted) 0f else 1.0f
    }

    /**
     * Detiene la reproducción actual.
     */
    fun stop() {
        exoPlayer?.stop()
    }

    /**
     * Libera los recursos del ExoPlayer al destruir la Activity o abandonar la vista.
     */
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
