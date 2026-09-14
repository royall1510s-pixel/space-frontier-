package com.spacefrontier.game.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.spacefrontier.game.audio.SoundManager
import com.spacefrontier.game.databinding.ActivityMainBinding
import com.spacefrontier.game.logic.GameEngine
import com.spacefrontier.game.models.GameState
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val gameEngine = GameEngine()
    private lateinit var soundManager: SoundManager
    private lateinit var gestureDetector: GestureDetector
    private val gameUpdateHandler = Handler(Looper.getMainLooper())
    private var lastUpdateTime = System.currentTimeMillis()
    private var engineStreamId = -1

    private val gameUpdateRunnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            val deltaTime = (currentTime - lastUpdateTime) / 1000f
            lastUpdateTime = currentTime

            gameEngine.update(deltaTime)
            updateUI()

            gameUpdateHandler.postDelayed(this, 16) // ~60 FPS
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicjalizuj Sound Manager
        soundManager = SoundManager(this)

        setupGestureDetector()
        setupUI()
        startGameLoop()

        Toast.makeText(this, "🚀 Gra załadowana! TAP ekran!", Toast.LENGTH_SHORT).show()
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                handleScreenTap()
                return true
            }
        })
    }

    private fun setupUI() {
        binding.gameContainer.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }

    private fun handleScreenTap() {
        val gameState = gameEngine.getGameState()
        
        when (gameState.gamePhase) {
            GameState.GamePhase.AWAITING_FIRST_TAP -> {
                // Uruchom silnik 1
                soundManager.playEngineStart()
                engineStreamId = soundManager.playEngineRunning()
                binding.tapHintText.text = "🔥 SILNIK 1 URUCHOMIONY!\nKLIKNIJ dla Stage 2"
                binding.stageIndicator.text = "Stage 1/3 - AKTYWNY ⚡"
            }

            GameState.GamePhase.STAGE_1_ACTIVE -> {
                // Uruchom silnik 2
                soundManager.playStage2Activation()
                binding.tapHintText.text = "🔥🔥 SILNIK 2 URUCHOMIONY!\nKLIKNIJ dla Stage 3"
                binding.stageIndicator.text = "Stage 2/3 - AKTYWNY ⚡"
            }

            GameState.GamePhase.STAGE_2_ACTIVE -> {
                // Uruchom silnik 3 - full power!
                soundManager.playStage3Activation()
                soundManager.stopEngineRunning(engineStreamId)
                binding.tapHintText.text = "🚀 PEŁNA MOC! AUTOMATYCZNE LĄDOWANIE..."
                binding.stageIndicator.text = "Stage 3/3 - PEŁNA MOC 🔥"
            }

            GameState.GamePhase.LANDED_SUCCESS -> {
                // Udane lądowanie - dzwięk sukcesu
                soundManager.playLandingSuccess()
                soundManager.playCoinReward()
                binding.tapHintText.text = "✅ LĄDOWANIE UDANE!\n+${gameState.currentPlanet?.reward} monet\n\nKLIKNIJ aby spróbować ponownie"
            }

            GameState.GamePhase.LANDED_FAILED -> {
                // Nie udane lądowanie
                soundManager.playLandingFail()
                binding.tapHintText.text = "❌ ZŁA WYSOKOŚĆ!\nProsimy spróbować ponownie\n\nKLIKNIJ aby retry"
            }

            else -> {}
        }

        gameEngine.handleTap()
    }

    private fun startGameLoop() {
        lastUpdateTime = System.currentTimeMillis()
        gameUpdateHandler.post(gameUpdateRunnable)
    }

    private fun updateUI() {
        val gameState = gameEngine.getGameState()
        val rocket = gameState.rocket
        val planet = gameState.currentPlanet

        // Aktualizuj 3D View rakiety
        binding.rocketView.updateRocket(rocket)

        // Aktualizuj liczniki
        binding.coinsText.text = "💰 ${gameState.totalCoins}"
        binding.velocityText.text = "${rocket.velocity.roundToInt()} km/h"
        binding.altitudeText.text = "${rocket.altitude.roundToInt()} km"
        binding.fuelText.text = "${rocket.fuel.roundToInt()}%"

        // Aktualizuj info o planecie
        if (planet != null) {
            binding.planetInfo.text = "${planet.emoji} ${planet.name}\nCel: ${planet.targetAltitude} km"
        }

        // Aktualizuj status
        when (gameState.gamePhase) {
            GameState.GamePhase.AWAITING_FIRST_TAP -> {
                binding.statusText.text = "🎯 TAP TO START"
                if (binding.tapHintText.text.isEmpty() || !binding.tapHintText.text.contains("SILNIK")) {
                    binding.tapHintText.text = "Kliknij ekran aby uruchomić rakietę"
                }
            }

            GameState.GamePhase.STAGE_1_ACTIVE -> {
                binding.statusText.text = "⚡ STAGE 1 ACTIVE"
            }

            GameState.GamePhase.STAGE_2_ACTIVE -> {
                binding.statusText.text = "⚡⚡ STAGE 2 ACTIVE"
            }

            GameState.GamePhase.STAGE_3_ACTIVE,
            GameState.GamePhase.IN_FLIGHT -> {
                binding.statusText.text = "🚀 W LOCIE..."
            }

            GameState.GamePhase.LANDING_SEQUENCE -> {
                binding.statusText.text = "🛬 SEKWENCJA LĄDOWANIA..."
            }

            GameState.GamePhase.LANDED_SUCCESS -> {
                binding.statusText.text = "✅ SUKCES!"
            }

            GameState.GamePhase.LANDED_FAILED -> {
                binding.statusText.text = "❌ NIEUDANE LĄDOWANIE"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameUpdateHandler.removeCallbacks(gameUpdateRunnable)
        soundManager.release()
    }
}
