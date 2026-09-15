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
import com.spacefrontier.game.logic.GameEngine
import com.spacefrontier.game.logic.FuelController
import com.spacefrontier.game.models.GameState
import kotlin.math.sin

class FuelAlarmView(
    context: Context
) : View(context) {

    private val handler =
        Handler(Looper.getMainLooper())

    private val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val borderPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var fuelPercent =
        100

    private var warning =
        ""

    private var phase:
        GameState.GamePhase? = null

    private var animationTime =
        0f

    private val updateRunnable =
        object : Runnable {

            override fun run() {

                updateFuel()

                animationTime +=
                    0.12f

                invalidate()

                handler.postDelayed(
                    this,
                    100
                )
            }
        }

    init {

        setLayerType(
            View.LAYER_TYPE_SOFTWARE,
            null
        )

        textPaint.typeface =
            Typeface.DEFAULT_BOLD

        borderPaint.style =
            Paint.Style.STROKE

        borderPaint.strokeWidth =
            4f

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

    private fun updateFuel() {

        val engine =
            getGameEngine()
                ?: return

        val state =
            engine.getGameState()

        phase =
            state.gamePhase

        val rocket =
            state.rocket

        fuelPercent =
            if (
                rocket.maxFuel > 0f
            ) {

                (
                    rocket.fuel /
                        rocket.maxFuel *
                        100f
                ).toInt()
                    .coerceIn(
                        0,
                        100
                    )

            } else {
                0
            }

        warning =
            FuelController
                .getWarning(
                    rocket
                )
                ?: ""
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        if (
            phase !=
            GameState.GamePhase.STAGE_1_ACTIVE &&
            phase !=
            GameState.GamePhase.STAGE_2_ACTIVE &&
            phase !=
            GameState.GamePhase.STAGE_3_ACTIVE &&
            phase !=
            GameState.GamePhase.IN_FLIGHT
        ) {

            return
        }

        if (
            warning.isEmpty()
        ) {

            return
        }

        val centerX =
            width / 2f

        val centerY =
            height / 2f

        val critical =
            fuelPercent <= 20

        val pulse =
            (
                sin(
                    animationTime * 8f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        val alpha =
            if (critical) {

                (
                    150f +
                        pulse * 105f
                ).toInt()

            } else {

                190
            }

        backgroundPaint.color =
            if (critical) {

                Color.argb(
                    alpha,
                    130,
                    0,
                    0
                )

            } else {

                Color.argb(
                    190,
                    120,
                    80,
                    0
                )
            }

        canvas.drawRoundRect(
            18f,
            centerY - 32f,
            width - 18f,
            centerY + 32f,
            18f,
            18f,
            backgroundPaint
        )

        borderPaint.color =
            if (critical) {

                Color.rgb(
                    255,
                    60,
                    60
                )

            } else {

                Color.rgb(
                    255,
                    210,
                    60
                )
            }

        canvas.drawRoundRect(
            18f,
            centerY - 32f,
            width - 18f,
            centerY + 32f,
            18f,
            18f,
            borderPaint
        )

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            19f

        textPaint.color =
            Color.WHITE

        canvas.drawText(
            warning,
            centerX,
            centerY - 5f,
            textPaint
        )

        textPaint.textSize =
            14f

        textPaint.color =
            if (critical) {

                Color.rgb(
                    255,
                    220,
                    220
                )

            } else {

                Color.rgb(
                    255,
                    240,
                    180
                )
            }

        canvas.drawText(
            "PALIWO: $fuelPercent%",
            centerX,
            centerY + 20f,
            textPaint
        )

        textPaint.textAlign =
            Paint.Align.LEFT
    }

    override fun onDetachedFromWindow() {

        handler.removeCallbacks(
            updateRunnable
        )

        super.onDetachedFromWindow()
    }
}
