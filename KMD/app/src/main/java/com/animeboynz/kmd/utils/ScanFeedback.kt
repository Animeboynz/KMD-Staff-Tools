package com.animeboynz.kmd.utils

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.delay

suspend fun playScanFeedback(context: Context, success: Boolean) {
    val vibrator = context.getSystemService(Vibrator::class.java)
    val effect = if (success) {
        VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
    } else {
        VibrationEffect.createWaveform(longArrayOf(0, 80, 60, 80), -1)
    }
    vibrator?.vibrate(effect)

    val tone = if (success) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_NACK
    val length = if (success) 120 else 220
    val generator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
    try {
        generator.startTone(tone, length)
        delay(length.toLong())
    } finally {
        generator.release()
    }
}
