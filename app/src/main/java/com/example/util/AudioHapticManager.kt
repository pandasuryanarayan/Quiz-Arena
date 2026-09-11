package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioHapticManager(private val context: Context) {

  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  private var toneGenerator: ToneGenerator? = try {
    ToneGenerator(AudioManager.STREAM_MUSIC, 85)
  } catch (e: Exception) {
    null
  }

  private val soundScope = CoroutineScope(Dispatchers.Default)

  var soundEnabled: Boolean = true
  var hapticEnabled: Boolean = true

  fun playOptionSelect() {
    vibrate(30, 80)
    playTone(ToneGenerator.TONE_PROP_BEEP, 50)
  }

  fun playCorrect() {
    vibratePattern(longArrayOf(0, 40, 60, 80), intArrayOf(0, 150, 0, 220))
    soundScope.launch {
      playTone(ToneGenerator.TONE_PROP_ACK, 120)
    }
  }

  fun playWrong() {
    vibrate(180, 180)
    soundScope.launch {
      playTone(ToneGenerator.TONE_PROP_NACK, 220)
    }
  }

  fun playTimeout() {
    vibratePattern(longArrayOf(0, 100, 80, 120), intArrayOf(0, 200, 0, 240))
    soundScope.launch {
      playTone(ToneGenerator.TONE_SUP_ERROR, 250)
    }
  }

  fun playStreakCombo(streak: Int) {
    vibratePattern(longArrayOf(0, 30, 40, 50, 40, 80), intArrayOf(0, 120, 0, 180, 0, 255))
    soundScope.launch {
      playTone(ToneGenerator.TONE_PROP_BEEP2, 160)
    }
  }

  fun playTimerWarning() {
    vibrate(20, 60)
    playTone(ToneGenerator.TONE_PROP_BEEP, 30)
  }

  fun playVictory() {
    vibratePattern(
      longArrayOf(0, 60, 80, 80, 80, 120),
      intArrayOf(0, 150, 0, 200, 0, 255)
    )
    soundScope.launch {
      playTone(ToneGenerator.TONE_PROP_ACK, 200)
    }
  }

  private fun playTone(toneType: Int, durationMs: Int) {
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(toneType, durationMs)
    } catch (_: Exception) {}
  }

  private fun vibrate(durationMs: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
    if (!hapticEnabled || vibrator?.hasVibrator() != true) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(durationMs)
      }
    } catch (_: Exception) {}
  }

  private fun vibratePattern(timings: LongArray, amplitudes: IntArray) {
    if (!hapticEnabled || vibrator?.hasVibrator() != true) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(timings, -1)
      }
    } catch (_: Exception) {}
  }

  fun release() {
    try {
      toneGenerator?.release()
      toneGenerator = null
    } catch (_: Exception) {}
  }
}
