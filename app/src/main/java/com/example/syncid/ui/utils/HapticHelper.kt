package com.example.syncid.ui.utils

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

class HapticHelper(private val context: Context) {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    fun playSuccess() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 200)
    }

    fun playError() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK, 500)
    }

    fun triggerSuccessHaptic(hapticFeedback: HapticFeedback) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun triggerErrorHaptic(hapticFeedback: HapticFeedback) {
        // Simulating double vibration for error
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        // Note: Compose HapticFeedback is limited, for complex patterns we'd use Vibrator service directly
    }
}
