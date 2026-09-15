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
import com.spacefrontier.game.models.GameState
import kotlin.math.sin

class AtmosphericEntryView(
    context: Context
) : View(context) {

    private val handler =
        Handler(Looper.getMainLooper())

    private val glowPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val panelPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val borderPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val titlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var phase:
        GameState.GamePhase? = null

    private var altitude =
        0f

    private var velocity =
        0f

    private var animationTime =
        0f

    private val updateRunnable =
        object : Runnable {

            override fun run() {

                updateState()

                animationTime +=
                    0.12f

                invalidate()

                handler.postDelayed(
                    this,
                    50
                )
            }
        }

    init {

        setLayerType(
            View.LAYER_TYPE_SOFTWARE,
            null
        )

        panelPaint.color =
            Color.argb(
                225,
                35,
                10,
                5
            )

        borderPaint.style =
            Paint.Style.STROKE

        borderPaint.strokeWidth =
            4f

        titlePaint.typeface =
            Typeface.DEFAULT_BOLD

        textPaint.typeface =
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

    private fun updateState() {

        val engine =
            getGameEngine()
                ?: return

        val state =
            engine.getGameState()

        phase =
            state.gamePhase

        altitude =
            state.rocket.altitude

        velocity =
            state.rocket.velocity
    }

    private fun isAtmosphericEntry():
            Boolean {

        return phase ==
            GameState.GamePhase.IN_FLIGHT &&
            altitude >= 70f
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        if (
            !isAtmosphericEntry()
        ) {
            return
        }

        val centerX =
            width / 2f

        val centerY =
            height / 2f

        drawHeatGlow(
            canvas,
            centerX,
            centerY
        )

        drawWarningPanel(
            canvas,
            centerX,
            centerY
        )

        drawHeatLines(
            canvas,
            centerX,
            centerY
        )
    }

    private fun drawHeatGlow(
        canvas: Canvas,
        centerX: Float,
        centerY: Float
    ) {

        val pulse =
            (
                sin(
                    animationTime * 7f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        val intensity =
            (
                35f +
                    pulse * 90f
                ).toInt()

        glowPaint.color =
            Color.argb(
                intensity,
                255,
                70,
                10
            )

        glowPaint.setShadowLayer(
            70f,
            0f,
            0f,
            Color.rgb(
                255,
                70,
                10
            )
        )

        canvas.drawCircle(
            centerX,
            centerY,
            80f +
                pulse * 30f,
            glowPaint
        )

        glowPaint.clearShadowLayer()
    }

    private fun drawWarningPanel(
        canvas: Canvas,
        centerX: Float,
        centerY: Float
    ) {

        val left =
            20f

        val right =
            width - 20f

        val top =
            centerY - 95f

        val bottom =
            centerY + 95f

        panelPaint.setShadowLayer(
            18f,
            0f,
            4f,
            Color.BLACK
        )

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            24f,
            24f,
            panelPaint
        )

        panelPaint.clearShadowLayer()

        borderPaint.color =
            Color.rgb(
                255,
                90,
                30
            )

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            24f,
            24f,
            borderPaint
        )

        titlePaint.textAlign =
            Paint.Align.CENTER

        titlePaint.textSize =
            25f

        titlePaint.color =
            Color.rgb(
                255,
                120,
                40
            )

        canvas.drawText(
            "🔥 WEJŚCIE W ATMOSFERĘ",
            centerX,
            centerY - 48f,
            titlePaint
        )

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            17f

        textPaint.color =
            Color.WHITE

        canvas.drawText(
            "OSŁONA TERMICZNA: AKTYWNA",
            centerX,
            centerY - 12f,
            textPaint
        )

        textPaint.textSize =
            15f

        textPaint.color =
            Color.rgb(
                255,
                205,
                150
            )

        canvas.drawText(
            "WYSOKOŚĆ: ${altitude.toInt()} km",
            centerX,
            centerY + 18f,
            textPaint
        )

        canvas.drawText(
            "PRĘDKOŚĆ: ${velocity.toInt()} km/h",
            centerX,
            centerY + 44f,
            textPaint
        )

        textPaint.textSize =
            13f

        textPaint.color =
            Color.rgb(
                255,
                160,
                120
            )

        canvas.drawText(
            "PRZYGOTOWANIE DO LĄDOWANIA",
            centerX,
            centerY + 70f,
            textPaint
        )
    }

    private fun drawHeatLines(
        canvas: Canvas,
        centerX: Float,
        centerY: Float
    ) {

        val linePaint =
            Paint(
                Paint.ANTI_ALIAS_FLAG
            )

        linePaint.strokeWidth =
            4f

        linePaint.color =
            Color.argb(
                150,
                255,
                100,
                30
            )

        val offset =
            (
                sin(
                    animationTime * 12f
                ) * 18f
            ).toFloat()

        canvas.drawLine(
            centerX - 115f,
            centerY + 55f + offset,
            centerX - 170f,
            centerY + 110f + offset,
            linePaint
        )

        canvas.drawLine(
            centerX + 115f,
            centerY + 55f - offset,
            centerX + 170f,
            centerY + 110f - offset,
            linePaint
        )

        canvas.drawLine(
            centerX - 80f,
            centerY + 65f - offset,
            centerX - 125f,
            centerY + 125f - offset,
            linePaint
        )

        canvas.drawLine(
            centerX + 80f,
            centerY + 65f + offset,
            centerX + 125f,
            centerY + 125f + offset,
            linePaint
        )
    }

    override fun onDetachedFromWindow() {

        handler.removeCallbacks(
            updateRunnable
        )

        super.onDetachedFromWindow()
    }
}
