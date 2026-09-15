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

    private val fuelBackgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val fuelPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val warningPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var rocket: Rocket? = null

    private var gamePhase:
            GameState.GamePhase? = null

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
            22f

        titlePaint.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

        valuePaint.color =
            Color.WHITE

        valuePaint.textSize =
            21f

        valuePaint.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

        smallPaint.color =
            Color.rgb(
                170,
                190,
                210
            )

        smallPaint.textSize =
            16f

        fuelBackgroundPaint.color =
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

        warningPaint.color =
            Color.rgb(
                255,
                70,
                60
            )

        warningPaint.textSize =
            22f

        warningPaint.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

        setLayerType(
            View.LAYER_TYPE_SOFTWARE,
            null
        )

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
            12f,
            0f,
            4f,
            Color.BLACK
        )

        canvas.drawRoundRect(
            12f,
            8f,
            panelWidth - 12f,
            panelHeight - 8f,
            18f,
            18f,
            backgroundPaint
        )

        backgroundPaint.clearShadowLayer()

        var y =
            34f

        canvas.drawText(
            "🚀 ${currentRocket.name}",
            24f,
            y,
            titlePaint
        )

        val stageText =
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
                    "LĄDOWANIE"

                GameState.GamePhase.LANDED_SUCCESS ->
                    "SUKCES"

                GameState.GamePhase.LANDED_FAILED ->
                    "AWARIA"

                else ->
                    "GOTOWOŚĆ"
            }

        canvas.drawText(
            stageText,
            panelWidth - 115f,
            y,
            valuePaint
        )

        y += 32f

        canvas.drawText(
            "🌍 WYSOKOŚĆ",
            24f,
            y,
            smallPaint
        )

        canvas.drawText(
            "${currentRocket.altitude.roundToInt()} km",
            24f,
            y + 24f,
            valuePaint
        )

        canvas.drawText(
            "💨 PRĘDKOŚĆ",
            panelWidth / 2f,
            y,
            smallPaint
        )

        canvas.drawText(
            "${currentRocket.velocity.roundToInt()}",
            panelWidth / 2f,
            y + 24f,
            valuePaint
        )

        y += 58f

        canvas.drawText(
            "⛽ PALIWO",
            24f,
            y,
            smallPaint
        )

        canvas.drawText(
            fuelStatus.percentageText,
            panelWidth - 75f,
            y,
            valuePaint
        )

        val barLeft =
            24f

        val barRight =
            panelWidth - 24f

        val barTop =
            y + 9f

        val barBottom =
            barTop + 14f

        canvas.drawRoundRect(
            barLeft,
            barTop,
            barRight,
            barBottom,
            7f,
            7f,
            fuelBackgroundPaint
        )

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

        val fuelWidth =
            (
                barRight -
                        barLeft
                ) *
                    fuelStatus.percentage

        canvas.drawRoundRect(
            barLeft,
            barTop,
            barLeft + fuelWidth,
            barBottom,
            7f,
            7f,
            fuelPaint
        )

        y += 39f

        canvas.drawText(
            "⛽ ${fuelStatus.displayText}",
            24f,
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
                panelWidth - 135f,
                y,
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
