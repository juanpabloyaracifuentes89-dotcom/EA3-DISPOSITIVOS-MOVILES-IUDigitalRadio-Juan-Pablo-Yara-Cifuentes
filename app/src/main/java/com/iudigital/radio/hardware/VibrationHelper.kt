package com.iudigital.radio.hardware

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

/**
 * RF-05: Retroalimentación Háptica (Vibración).
 * Utilidad robusta para emitir vibraciones claramente perceptibles en cualquier dispositivo Android,
 * incluyendo capas como MIUI/HyperOS (Xiaomi/Redmi) y versiones desde Android 7 hasta Android 14+.
 */
class VibrationHelper(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            Log.e("VibrationHelper", "Error obteniendo Vibrator: ${e.message}")
            null
        }
    }

    /**
     * Emite una pulsación háptica corta y nítida (click) con potencia suficiente para ser sentida.
     * @param durationMs Duración en milisegundos (por defecto 80ms)
     */
    fun vibrateClick(durationMs: Long = 80L) {
        try {
            val v = vibrator ?: return
            if (v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Intenta usar efecto predefinido de click o pulso directo con máxima amplitud
                    try {
                        val effect = VibrationEffect.createOneShot(durationMs, 255)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val attrs = VibrationAttributes.Builder()
                                .setUsage(VibrationAttributes.USAGE_TOUCH)
                                .build()
                            v.vibrate(effect, attrs)
                        } else {
                            v.vibrate(effect)
                        }
                    } catch (_: Exception) {
                        val fallbackEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        v.vibrate(fallbackEffect)
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                    v.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(durationMs)
                }
            }
        } catch (e: Exception) {
            Log.e("VibrationHelper", "Error al ejecutar vibrateClick: ${e.localizedMessage}")
        }
    }

    /**
     * Emite una pulsación háptica doble para acciones de mute o cambios destacados.
     */
    fun vibrateDouble() {
        try {
            val v = vibrator ?: return
            if (v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 70, 70, 70)
                    val amplitudes = intArrayOf(0, 255, 0, 255)
                    val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val attrs = VibrationAttributes.Builder()
                            .setUsage(VibrationAttributes.USAGE_TOUCH)
                            .build()
                        v.vibrate(effect, attrs)
                    } else {
                        v.vibrate(effect)
                    }
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(150L)
                }
            }
        } catch (e: Exception) {
            Log.e("VibrationHelper", "Error en vibrateDouble: ${e.localizedMessage}")
        }
    }
}
