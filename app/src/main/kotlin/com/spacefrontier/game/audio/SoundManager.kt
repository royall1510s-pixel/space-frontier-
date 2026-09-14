package com.spacefrontier.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

/**
 * Manager dźwięków gry
 * Obsługuje efekty dźwiękowe silników, lądowania i sukcesów
 */
class SoundManager(context: Context) {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(10)
        .setAudioAttributes(audioAttributes)
        .build()

    // Sound IDs
    private var engineStartSoundId = -1
    private var engineRunSoundId = -1
    private var landingSuccessSoundId = -1
    private var landingFailSoundId = -1
    private var stage2SoundId = -1
    private var stage3SoundId = -1
    private var coinSoundId = -1

    private val TAG = "SoundManager"

    init {
        loadSounds(context)
    }

    private fun loadSounds(context: Context) {
        try {
            // Symulacja ładowania dźwięków
            // W rzeczywistości załadowałbyś pliki z assets/
            Log.d(TAG, "Sounds would be loaded from assets/sounds/")
            
            // Tutaj bylibyśmy wykorzystywać context.assets.openFd()
            // ale dla demonstracji generujemy syntetyczne dźwięki
            createSyntheticSounds()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading sounds: ${e.message}")
        }
    }

    private fun createSyntheticSounds() {
        // W produkcji załadowałbyś rzeczywiste pliki audio
        // Tu tylko logujemy że byśmy zaladowali
        Log.d(TAG, "Synthetic sounds initialized")
    }

    /**
     * Dźwięk startu silnika
     */
    fun playEngineStart() {
        Log.d(TAG, "🔊 Playing engine start sound")
        // soundPool.play(engineStartSoundId, 1f, 1f, 1, 0, 1f)
    }

    /**
     * Dźwięk pracy silnika (pętla)
     */
    fun playEngineRunning(): Int {
        Log.d(TAG, "🔊 Playing engine running sound")
        // return soundPool.play(engineRunSoundId, 0.8f, 0.8f, 1, -1, 0.8f)
        return -1
    }

    /**
     * Zatrzymaj dźwięk silnika
     */
    fun stopEngineRunning(streamId: Int) {
        if (streamId >= 0) {
            Log.d(TAG, "🔊 Stopping engine sound")
            soundPool.stop(streamId)
        }
    }

    /**
     * Dźwięk aktywacji Stage 2
     */
    fun playStage2Activation() {
        Log.d(TAG, "🔊 Playing Stage 2 activation sound")
        // soundPool.play(stage2SoundId, 0.9f, 0.9f, 1, 0, 1f)
    }

    /**
     * Dźwięk aktywacji Stage 3
     */
    fun playStage3Activation() {
        Log.d(TAG, "🔊 Playing Stage 3 activation sound")
        // soundPool.play(stage3SoundId, 1f, 1f, 1, 0, 1f)
    }

    /**
     * Dźwięk udanego lądowania
     */
    fun playLandingSuccess() {
        Log.d(TAG, "🔊 Playing landing success sound - ✅ DING DING!")
        // soundPool.play(landingSuccessSoundId, 1f, 1f, 1, 0, 1f)
    }

    /**
     * Dźwięk nieudanego lądowania
     */
    fun playLandingFail() {
        Log.d(TAG, "🔊 Playing landing fail sound - ❌ BOOM!")
        // soundPool.play(landingFailSoundId, 0.7f, 0.7f, 1, 0, 1f)
    }

    /**
     * Dźwięk zdobycia monet
     */
    fun playCoinReward() {
        Log.d(TAG, "🔊 Playing coin reward sound - CHING!")
        // soundPool.play(coinSoundId, 0.6f, 0.6f, 1, 0, 1f)
    }

    /**
     * Zwolnij zasoby
     */
    fun release() {
        soundPool.release()
        Log.d(TAG, "Sound manager released")
    }
}
