package com.spacefrontier.game.ui

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.View
import com.spacefrontier.game.logic.FuelStatus
import com.spacefrontier.game.logic.GameEngine
import com.spacefrontier.game.models.GameState
import com.spacefrontier.game.models.Rocket
import kotlin.math.roundToInt

class FlightHudView(
    context: Context
) : View(context) {

    private val handler =
        Handler(Looper.getMainLooper())

    private val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val titlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val valuePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val smallPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val barBackgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val fuelPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val progressPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val warningPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var rocket: Rocket? = null

    private var gamePhase:
            GameState.GamePhase? = null

    private var targetAltitude =
        0f

    private var fuelStatus =
        FuelStatus.from(
            fuel = 0f,
            maxFuel = 0f
        )

    private val updateRunnable =
        object : Runnable {

            override fun run() {

                updateFromGameEngine()

                handler.postDelayed(
                    this,
                    100
                )
            }
        }

    init {

        backgroundPaint.color =
            Color.rgb(
                8,
                18,
                32
            )

        titlePaint.color =
            Color.WHITE

        titlePaint.textSize =
            21f

        titlePaint.typeface =
            Typeface.DEFAULT_BOLD

        valuePaint.color =
            Color.WHITE

        valuePaint.textSize =
            18f

        valuePaint.typeface =
            Typeface.DEFAULT_BOLD

        smallPaint.color =
            Color.rgb(
                170,
                190,
                210
            )

        smallPaint.textSize =
            14f

        barBackgroundPaint.color =
            Color.rgb(
                35,
                48,
                65
            )

        fuelPaint.color =
            Color.rgb(
                70,
                230,
                140
            )

        progressPaint.color =
            Color.rgb(
                70,
                170,
                255
            )

        warningPaint.color =
            Color.rgb(
                255,
                70,
                60
            )

        warningPaint.textSize =
            18f

        warningPaint.typeface =
            Typeface.DEFAULT_BOLD

        handler.post(
            updateRunnable
        )
    }

    private fun getGameEngine():
            GameEngine? {

        val activity =
            context as? Activity
                ?: return null

        return try {

            val field =
                activity.javaClass
                    .getDeclaredField(
                        "gameEngine"
                    )

            field.isAccessible =
                true

            field.get(activity)
                    as? GameEngine

        } catch (
            exception: Exception
        ) {

            null
        }
    }

    private fun updateFromGameEngine() {

        val engine =
            getGameEngine()
                ?: return

        val state =
            engine.getGameState()

        rocket =
            state.rocket

        gamePhase =
            state.gamePhase

        targetAltitude =
            state.currentPlanet
                ?.targetAltitude
                ?: 0f

        fuelStatus =
            FuelStatus.from(
                fuel =
                    state.rocket.fuel,
                maxFuel =
                    state.rocket.maxFuel
            )

        invalidate()
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        val currentRocket =
            rocket
                ?: return

        val panelWidth =
            width.toFloat()

        val panelHeight =
            height.toFloat()

        backgroundPaint.setShadowLayer(
            10f,
            0f,
            3f,
            Color.BLACK
        )

        canvas.drawRoundRect(
            10f,
            6f,
            panelWidth - 10f,
            panelHeight - 6f,
            16f,
            16f,
            backgroundPaint
        )

        backgroundPaint.clearShadowLayer()

        var y =
            28f

        canvas.drawText(
            "🚀 ${currentRocket.name}",
            20f,
            y,
            titlePaint
        )

        val phaseText =
            when (
                gamePhase
            ) {

                GameState.GamePhase.STAGE_1_ACTIVE ->
                    "STAGE 1"

                GameState.GamePhase.STAGE_2_ACTIVE ->
                    "STAGE 2"

                GameState.GamePhase.STAGE_3_ACTIVE ->
                    "STAGE 3"

                GameState.GamePhase.IN_FLIGHT ->
                    "LOT"

                GameState.GamePhase.LANDING_SEQUENCE ->
                    "🛬 LĄDOWANIE"

                GameState.GamePhase.LANDED_SUCCESS ->
                    "SUKCES"

                GameState.GamePhase.LANDED_FAILED ->
                    "AWARIA"

                else ->
                    "GOTOWOŚĆ"
            }

        canvas.drawText(
            phaseText,
            panelWidth - 110f,
            y,
            valuePaint
        )

        y += 29f

        val altitude =
            currentRocket.altitude

        val altitudeTarget =
            targetAltitude
                .coerceAtLeast(
                    1f
                )

        val altitudeProgress =
            (
                altitude /
                        altitudeTarget
                ).coerceIn(
                    0f,
                    1f
                )

        canvas.drawText(
            "🌍 ${altitude.roundToInt()} km / " +
                    "${targetAltitude.roundToInt()} km",
            20f,
            y,
            smallPaint
        )

        val altitudeBarLeft =
            20f

        val altitudeBarRight =
            panelWidth - 20f

        val altitudeBarTop =
            y + 7f

        val altitudeBarBottom =
            altitudeBarTop + 9f

        canvas.drawRoundRect(
            altitudeBarLeft,
            altitudeBarTop,
            altitudeBarRight,
            altitudeBarBottom,
            5f,
            5f,
            barBackgroundPaint
        )

        canvas.drawRoundRect(
            altitudeBarLeft,
            altitudeBarTop,
            altitudeBarLeft +
                    (
                        altitudeBarRight -
                                altitudeBarLeft
                        ) *
                        altitudeProgress,
            altitudeBarBottom,
            5f,
            5f,
            progressPaint
        )

        y += 31f

        canvas.drawText(
            "💨 ${currentRocket.velocity.roundToInt()} km/h",
            20f,
            y,
            valuePaint
        )

        canvas.drawText(
            "⚡ ${currentRocket.acceleration.roundToInt()}",
            panelWidth / 2f,
            y,
            smallPaint
        )

        y += 25f

        canvas.drawText(
            "⛽ ${fuelStatus.percentageText}",
            20f,
            y,
            smallPaint
        )

        val fuelBarLeft =
            20f

        val fuelBarRight =
            panelWidth - 20f

        val fuelBarTop =
            y + 7f

        val fuelBarBottom =
            fuelBarTop + 11f

        fuelPaint.color =
            when {

                fuelStatus.isCritical ->
                    Color.rgb(
                        255,
                        55,
                        45
                    )

                fuelStatus.isLow ->
                    Color.rgb(
                        255,
                        190,
                        45
                    )

                else ->
                    Color.rgb(
                        70,
                        230,
                        140
                    )
            }

        canvas.drawRoundRect(
            fuelBarLeft,
            fuelBarTop,
            fuelBarRight,
            fuelBarBottom,
            6f,
            6f,
            barBackgroundPaint
        )

        canvas.drawRoundRect(
            fuelBarLeft,
            fuelBarTop,
            fuelBarLeft +
                    (
                        fuelBarRight -
                                fuelBarLeft
                        ) *
                        fuelStatus.percentage,
            fuelBarBottom,
            6f,
            6f,
            fuelPaint
        )

        y += 32f

        canvas.drawText(
            "⛽ ${fuelStatus.displayText}",
            20f,
            y,
            smallPaint
        )

        if (
            fuelStatus.isCritical &&
            gamePhase !=
            GameState.GamePhase.LANDED_SUCCESS &&
            gamePhase !=
            GameState.GamePhase.LANDED_FAILED
        ) {

            canvas.drawText(
                "⚠ LOW FUEL",
                panelWidth - 120f,
                y,
                warningPaint
            )
        }

        if (
            gamePhase ==
            GameState.GamePhase.LANDING_SEQUENCE
        ) {

            canvas.drawText(
                "🛬 AUTOMATYCZNE LĄDOWANIE",
                20f,
                panelHeight - 12f,
                warningPaint
            )
        }
    }

    override fun onDetachedFromWindow() {

        handler.removeCallbacks(
            updateRunnable
        )

        super.onDetachedFromWindow()
    }
}
