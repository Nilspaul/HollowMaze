package com.hollowmaze.game.controller.helper

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)

/**
 * Interface, um API-Spezifisch den VibrationsManager anzusteuern
 */
interface VibrationProvider {
    fun vibrate(duration: Long)
}

/**
 * Provider für API < 31
 */
class VibratorOldProvider(private val vibrator: Vibrator) : VibrationProvider {
    override fun vibrate(duration: Long) {
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(duration)
            }
        }
    }
}

/**
 * Provider für API >= 31
 */
class VibratorNewProvider(private val vibratorManager: VibratorManager) : VibrationProvider {
    override fun vibrate(duration: Long) {
        val vibrator = vibratorManager.defaultVibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}

/**
 * Oberklasse, um die Vibration anzusteuern
 */
class VibrationHelper(context: Context) {
    private val provider: VibrationProvider

    init {
        provider = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            VibratorNewProvider(vibratorManager)
        } else {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            VibratorOldProvider(vibrator)
        }
    }

    /**
     * Vibration auslösen mit Dauer in Millisekunden
     * @param duraction Long - in Millisekunden
     */
    fun vibrate(duration: Long) {
        provider.vibrate(duration)
    }
}