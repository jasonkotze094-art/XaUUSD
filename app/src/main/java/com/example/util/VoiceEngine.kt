package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class VoiceEngine(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var soundPool: SoundPool? = null
    private var isVoiceEnabled = true

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            soundPool = SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build()
        } catch (e: Exception) {
            Log.e("VoiceEngine", "Error initializing TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setPitch(0.92f) // Slightly robotic, assertive mecha voice
                    engine.setSpeechRate(1.05f)
                    isTtsInitialized = true
                }
            }
        }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        isVoiceEnabled = enabled
    }

    fun isVoiceEnabled(): Boolean = isVoiceEnabled

    fun speak(text: String) {
        if (!isVoiceEnabled) return
        if (isTtsInitialized && tts != null) {
            try {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "VoiceCue_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                Log.e("VoiceEngine", "Speak failure", e)
            }
        }
    }

    fun speakTradingActivated() {
        speak("Trading activated. Money engine on.")
    }

    fun speakTradingDeactivated() {
        speak("Trading deactivated. EA standing by.")
    }

    fun speakModeChanged(mode: String) {
        speak("Execution mode switched to $mode.")
    }

    fun speakSignalGenerated(symbol: String, type: String) {
        speak("High probability $type signal detected on $symbol.")
    }

    fun cleanup() {
        try {
            tts?.stop()
            tts?.shutdown()
            soundPool?.release()
        } catch (e: Exception) {
            Log.e("VoiceEngine", "Cleanup error", e)
        }
    }
}
