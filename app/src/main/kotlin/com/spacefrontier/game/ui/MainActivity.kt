package com.spacefrontier.game.ui

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.spacefrontier.game.audio.SoundManager
import com.spacefrontier.game.databinding.ActivityMainBinding
import com.spacefrontier.game.logic.GameEngine
import com.spacefrontier.game.logic.MissionRequirementChecker
import com.spacefrontier.game.models.GameState
import com.spacefrontier.game.models.Rocket
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var gameEngine: GameEngine

    private lateinit var soundManager: SoundManager

    private lateinit var gestureDetector: GestureDetector

    private val gameUpdateHandler =
        Handler(Looper.getMainLooper())

    private var lastUpdateTime =
        System.currentTimeMillis()

    private var engineStreamId =
        -1

    private var lastShownReward =
        -1

    private val gameUpdateRunnable =
        object : Runnable {

            override fun run() {

                val currentTime =
                    System.currentTimeMillis()

                val deltaTime =
                    (
                        currentTime -
                                lastUpdateTime
                        ) / 1000f

                lastUpdateTime =
                    currentTime

                gameEngine.update(
                    deltaTime
                )

                updateUI()

                gameUpdateHandler.postDelayed(
                    this,
                    16
                )
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        binding =
            ActivityMainBinding.inflate(
                layoutInflater
            )

        setContentView(
            binding.root
        )

        gameEngine =
            GameEngine(this)

        soundManager =
            SoundManager(this)

        setupRocketTouch()

        setupHangar()

        setupStartButton()

        setupPlanetSelector()

        updateUI()

        startGameLoop()
    }

    private fun setupRocketTouch() {

        gestureDetector =
            GestureDetector(
                this,
                object :
                    GestureDetector.SimpleOnGestureListener() {

                    override fun onDown(
                        e: MotionEvent
                    ): Boolean {

                        return true
                    }

                    override fun onSingleTapUp(
                        e: MotionEvent
                    ): Boolean {

                        handleFlightTap()

                        return true
                    }
                }
            )

        binding.rocketView.setOnTouchListener {
                _,
                event ->

            gestureDetector.onTouchEvent(
                event
            )
        }
    }

    private fun setupStartButton() {

        binding.tapHintText.setOnClickListener {

            val phase =
                gameEngine
                    .getGameState()
                    .gamePhase

            if (
                phase ==
                GameState.GamePhase.AWAITING_FIRST_TAP
            ) {

                handleFlightTap()
            }

            if (
                phase ==
                GameState.GamePhase.LANDED_SUCCESS ||
                phase ==
                GameState.GamePhase.LANDED_FAILED
            ) {

                handleFlightTap()
            }
        }
    }

    private fun setupPlanetSelector() {

        binding.planetInfo.setOnClickListener {

            PlanetSelector.show(
                context = this,
                progress =
                    gameEngine.getPlanetProgress(),
                currentIndex =
                    gameEngine.getCurrentPlanetIndex()
            ) { planetIndex ->

                if (
                    gameEngine.selectPlanet(
                        planetIndex
                    )
                ) {

                    updateUI()

                    Toast.makeText(
                        this,
                        "🪐 Wybrano planetę",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "🛑 Tej planety nie można teraz wybrać.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun handleFlightTap() {

        val gameState =
            gameEngine.getGameState()

        when (
            gameState.gamePhase
        ) {

            GameState.GamePhase.AWAITING_FIRST_TAP -> {

                val planet =
                    gameState.currentPlanet

                if (planet == null) {

                    Toast.makeText(
                        this,
                        "🛑 Nie wybrano planety.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return
                }

                val requirement =
                    MissionRequirementChecker.check(
                        rocket = gameState.rocket,
                        planet = planet
                    )

                if (!requirement.canStart) {

                    binding.tapHintText.text =
                        "🛑 RAKIETA ZA SŁABA"

                    Toast.makeText(
                        this,
                        requirement.message,
                        Toast.LENGTH_LONG
                    ).show()

                    return
                }

                soundManager.playEngineStart()

                engineStreamId =
                    soundManager.playEngineRunning()

                binding.tapHintText.text =
                    "🔥 SILNIK 1 URUCHOMIONY!"

                gameEngine.handleTap()
            }

            GameState.GamePhase.STAGE_1_ACTIVE -> {

                soundManager.playStage2Activation()

                binding.tapHintText.text =
                    "🔥🔥 STAGE 2!"

                gameEngine.handleTap()
            }

            GameState.GamePhase.STAGE_2_ACTIVE -> {

                soundManager.playStage3Activation()

                soundManager.stopEngineRunning(
                    engineStreamId
                )

                binding.tapHintText.text =
                    "🚀 PEŁNA MOC!"

                gameEngine.handleTap()
            }

            GameState.GamePhase.LANDED_SUCCESS -> {

                soundManager.playLandingSuccess()

                soundManager.playCoinReward()

                gameEngine.handleTap()

                setupHangar()
            }

            GameState.GamePhase.LANDED_FAILED -> {

                soundManager.playLandingFail()

                gameEngine.handleTap()

                setupHangar()
            }

            else -> Unit
        }
    }

    private fun setupHangar() {

        binding.rocketListContainer
            .removeAllViews()

        gameEngine
            .getRocketCatalog()
            .forEach { rocket ->

                addRocketCard(
                    rocket
                )
            }
    }

    private fun addRocketCard(
        rocket: Rocket
    ) {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            16,
            12,
            16,
            12
        )

        val selected =
            gameEngine
                .getSelectedRocket()
                .id ==
                    rocket.id

        if (selected) {

            card.setBackgroundColor(
                Color.rgb(
                    18,
                    55,
                    65
                )
            )

        } else {

            card.setBackgroundColor(
                Color.rgb(
                    16,
                    24,
                    42
                )
            )
        }

        val name =
            TextView(this)

        name.text =
            if (rocket.unlocked) {

                if (selected) {

                    "✅ ${rocket.name}"

                } else {

                    "🚀 ${rocket.name}"
                }

            } else {

                "🔒 ${rocket.name}"
            }

        name.setTextColor(
            Color.WHITE
        )

        name.textSize =
            17f

        name.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        card.addView(
            name
        )

        val stats =
            TextView(this)

        stats.text =
            "⚡ ${rocket.thrust.roundToInt()}  " +
                    "⛽ ${rocket.maxFuel.roundToInt()}  " +
                    "🌍 ${rocket.maxAltitude.roundToInt()} km"

        stats.setTextColor(
            Color.LTGRAY
        )

        stats.textSize =
            12f

        val statsParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        statsParams.topMargin =
            4

        card.addView(
            stats,
            statsParams
        )

        if (rocket.unlocked) {

            addUpgradePanel(
                card,
                rocket
            )
        }

        val action =
            TextView(this)

        action.gravity =
            Gravity.CENTER

        action.setPadding(
            10,
            8,
            10,
            8
        )

        action.textSize =
            14f

        val actionParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        actionParams.topMargin =
            8

        if (rocket.unlocked) {

            action.text =
                if (selected) {

                    "✓ WYBRANA"

                } else {

                    "WYBIERZ"
                }

            action.setTextColor(
                Color.WHITE
            )

            action.setBackgroundColor(
                Color.rgb(
                    30,
                    100,
                    150
                )
            )

            action.setOnClickListener {

                if (
                    gameEngine.selectRocket(
                        rocket.id
                    )
                ) {

                    setupHangar()

                    updateUI()

                    Toast.makeText(
                        this,
                        "🚀 Wybrano ${rocket.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } else {

            action.text =
                "💰 KUP  ${rocket.price}"

            action.setTextColor(
                Color.WHITE
            )

            action.setBackgroundColor(
                Color.rgb(
                    80,
                    70,
                    20
                )
            )

            action.setOnClickListener {

                val success =
                    gameEngine.buyRocket(
                        rocket.id
                    )

                if (success) {

                    setupHangar()

                    updateUI()

                    Toast.makeText(
                        this,
                        "🚀 ${rocket.name} odblokowana!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "💰 Za mało monet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        card.addView(
            action,
            actionParams
        )

        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        cardParams.bottomMargin =
            8

        binding.rocketListContainer
            .addView(
                card,
                cardParams
            )
    }

    private fun addUpgradePanel(
        card: LinearLayout,
        rocket: Rocket
    ) {

        val upgrade =
            gameEngine.getRocketUpgrade(
                rocket.id
            )

        val title =
            TextView(this)

        title.text =
            "🔧 ULEPSZENIA"

        title.setTextColor(
            Color.rgb(
                255,
                170,
                70
            )
        )

        title.textSize =
            13f

        title.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        val titleParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        titleParams.topMargin =
            10

        card.addView(
            title,
            titleParams
        )

        val row =
            LinearLayout(this)

        row.orientation =
            LinearLayout.HORIZONTAL

        row.gravity =
            Gravity.CENTER

        val rowParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        rowParams.topMargin =
            6

        card.addView(
            row,
            rowParams
        )

        addUpgradeButton(
            row,
            "⚡\nSILNIK\nLv.${upgrade.engineLevel}\n💰 ${upgrade.engineCost}",
            rocket.id,
            UpgradeType.ENGINE
        )

        addUpgradeButton(
            row,
            "⛽\nPALIWO\nLv.${upgrade.fuelLevel}\n💰 ${upgrade.fuelCost}",
            rocket.id,
            UpgradeType.FUEL
        )

        addUpgradeButton(
            row,
            "🌍\nZASIĘG\nLv.${upgrade.altitudeLevel}\n💰 ${upgrade.altitudeCost}",
            rocket.id,
            UpgradeType.ALTITUDE
        )
    }

    private enum class UpgradeType {
        ENGINE,
        FUEL,
        ALTITUDE
    }

    private fun addUpgradeButton(
        row: LinearLayout,
        textValue: String,
        rocketId: Int,
        type: UpgradeType
    ) {

        val button =
            TextView(this)

        button.text =
            textValue

        button.gravity =
            Gravity.CENTER

        button.setTextColor(
            Color.WHITE
        )

        button.textSize =
            11f

        button.setPadding(
            4,
            8,
            4,
            8
        )

        button.setBackgroundColor(
            Color.rgb(
                35,
                60,
                85
            )
        )

        val params =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

        params.setMargins(
            3,
            0,
            3,
            0
        )

        row.addView(
            button,
            params
        )

        button.setOnClickListener {

            val gameState =
                gameEngine.getGameState()

            val phase =
                gameState.gamePhase

            if (
                phase !=
                GameState.GamePhase.AWAITING_FIRST_TAP &&
                phase !=
                GameState.GamePhase.LANDED_SUCCESS &&
                phase !=
                GameState.GamePhase.LANDED_FAILED
            ) {

                Toast.makeText(
                    this,
                    "🛑 Zakończ misję przed ulepszaniem.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val success =
                when (type) {

                    UpgradeType.ENGINE ->
                        gameEngine.upgradeEngine(
                            rocketId
                        )

                    UpgradeType.FUEL ->
                        gameEngine.upgradeFuel(
                            rocketId
                        )

                    UpgradeType.ALTITUDE ->
                        gameEngine.upgradeAltitude(
                            rocketId
                        )
                }

            if (success) {

                setupHangar()

                updateUI()

                Toast.makeText(
                    this,
                    "🔧 Ulepszenie zakupione!",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "💰 Za mało monet!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startGameLoop() {

        lastUpdateTime =
            System.currentTimeMillis()

        gameUpdateHandler.post(
            gameUpdateRunnable
        )
    }

    private fun updateRewardPanel(
        gameState: GameState
    ) {

        when (
            gameState.gamePhase
        ) {

            GameState.GamePhase.LANDED_SUCCESS -> {

                binding.rewardPanel.visibility =
                    android.view.View.VISIBLE

                binding.rewardTitle.text =
                    "🏆 MISJA UDANA!"

                val planet =
                    gameState.currentPlanet

                if (planet != null) {

                    binding.rewardPlanet.text =
                        "${planet.emoji} ${planet.name.uppercase()}"
                }

                binding.rewardAmount.text =
                    "💰 +${gameState.missionReward} MONET"

                binding.rewardBalance.text =
                    "SALDO: 💰 ${gameState.totalCoins}"

                lastShownReward =
                    gameState.missionReward
            }

            GameState.GamePhase.LANDED_FAILED -> {

                binding.rewardPanel.visibility =
                    android.view.View.VISIBLE

                binding.rewardTitle.text =
                    "❌ MISJA NIEUDANA"

                val planet =
                    gameState.currentPlanet

                if (planet != null) {

                    binding.rewardPlanet.text =
                        "${planet.emoji} ${planet.name.uppercase()}"
                }

                binding.rewardAmount.text =
                    "💰 +0 MONET"

                binding.rewardBalance.text =
                    "SALDO: 💰 ${gameState.totalCoins}"
            }

            else -> {

                binding.rewardPanel.visibility =
                    android.view.View.GONE
            }
        }
    }

    private fun updateUI() {

        val gameState =
            gameEngine.getGameState()

        val rocket =
            gameState.rocket

        val planet =
            gameState.currentPlanet

        binding.rocketView.updateScene(
            rocket,
            planet,
            gameState.gamePhase
        )

        binding.coinsText.text =
            "💰 ${gameState.totalCoins}"

        binding.velocityText.text =
            "${rocket.velocity.roundToInt()} km/h"

        binding.altitudeText.text =
            "${rocket.altitude.roundToInt()} km"

        binding.fuelText.text =
            "${rocket.fuel.roundToInt()}"

        binding.stageIndicator.text =
            when (
                gameState.gamePhase
            ) {

                GameState.GamePhase.AWAITING_FIRST_TAP ->
                    "${rocket.name} • GOTOWA"

                GameState.GamePhase.STAGE_1_ACTIVE ->
                    "${rocket.name} • STAGE 1/3"

                GameState.GamePhase.STAGE_2_ACTIVE ->
                    "${rocket.name} • STAGE 2/3"

                GameState.GamePhase.STAGE_3_ACTIVE ->
                    "${rocket.name} • STAGE 3/3"

                GameState.GamePhase.IN_FLIGHT ->
                    "${rocket.name} • W LOCIE"

                GameState.GamePhase.LANDING_SEQUENCE ->
                    "${rocket.name} • 🛬 LĄDOWANIE"

                GameState.GamePhase.LANDED_SUCCESS ->
                    "${rocket.name} • ✅ SUKCES"

                GameState.GamePhase.LANDED_FAILED ->
                    "${rocket.name} • ❌ AWARIA"
            }

        if (planet != null) {

            binding.planetInfo.text =
                "${planet.emoji} ${planet.name} • " +
                        "Cel: ${planet.targetAltitude.roundToInt()} km\n" +
                        "🪐 DOTKNIJ, ABY WYBRAĆ PLANETĘ"
        }

        binding.missionPanel.setMission(
            gameEngine.getCurrentPlanetMission(),
            gameState.rocket
        )

        binding.statusText.text =
            when (
                gameState.gamePhase
            ) {

                GameState.GamePhase.AWAITING_FIRST_TAP ->
                    "${rocket.name}"

                GameState.GamePhase.STAGE_1_ACTIVE ->
                    "⚡ STAGE 1 ACTIVE"

                GameState.GamePhase.STAGE_2_ACTIVE ->
                    "⚡⚡ STAGE 2 ACTIVE"

                GameState.GamePhase.STAGE_3_ACTIVE ->
                    "🚀 PEŁNA MOC"

                GameState.GamePhase.IN_FLIGHT ->
                    "🚀 W LOCIE"

                GameState.GamePhase.LANDING_SEQUENCE ->
                    "🛬 LĄDOWANIE"

                GameState.GamePhase.LANDED_SUCCESS ->
                    "✅ MISJA UDANA"

                GameState.GamePhase.LANDED_FAILED ->
                    "❌ MISJA NIEUDANA"
            }

        when (
            gameState.gamePhase
        ) {

            GameState.GamePhase.AWAITING_FIRST_TAP -> {

                binding.tapHintText.text =
                    "▶  START MISJI"
            }

            GameState.GamePhase.STAGE_1_ACTIVE -> {

                binding.tapHintText.text =
                    "KLIKNIJ RAKIETĘ → STAGE 2"
            }

            GameState.GamePhase.STAGE_2_ACTIVE -> {

                binding.tapHintText.text =
                    "KLIKNIJ RAKIETĘ → STAGE 3"
            }

            GameState.GamePhase.STAGE_3_ACTIVE,
            GameState.GamePhase.IN_FLIGHT -> {

                binding.tapHintText.text =
                    "🚀 RAKIETA W LOCIE..."
            }

            GameState.GamePhase.LANDING_SEQUENCE -> {

                binding.tapHintText.text =
                    "🛬 AUTOMATYCZNE LĄDOWANIE..."
            }

            GameState.GamePhase.LANDED_SUCCESS -> {

                binding.tapHintText.text =
                    "🚀 NASTĘPNA MISJA"
            }

            GameState.GamePhase.LANDED_FAILED -> {

                binding.tapHintText.text =
                    "🔄 SPRÓBUJ PONOWNIE"
            }
        }

        updateRewardPanel(
            gameState
        )
    }

    override fun onDestroy() {

        super.onDestroy()

        gameUpdateHandler.removeCallbacks(
            gameUpdateRunnable
        )

        soundManager.release()
    }
}
