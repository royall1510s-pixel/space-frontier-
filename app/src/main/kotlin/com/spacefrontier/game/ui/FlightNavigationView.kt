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
import kotlin.math.cos
import kotlin.math.sin

class FlightNavigationView(
    context: Context
) : View(context) {

    private val handler =
        Handler(Looper.getMainLooper())

    private val panelPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val borderPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val titlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val smallPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val radarPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val progressPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var phase:
        GameState.GamePhase? = null

    private var altitude =
        0f

    private var velocity =
        0f

    private var targetAltitude =
        1f

    private var planetName =
        "CEL"

    private var animationTime =
        0f

    private val updateRunnable =
        object : Runnable {

            override fun run() {

                updateState()

                animationTime +=
                    0.08f

                invalidate()

                handler.postDelayed(
                    this,
                    60
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
                220,
                5,
                18,
                35
            )

        borderPaint.style =
            Paint.Style.STROKE

        borderPaint.strokeWidth =
            3f

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

        targetAltitude =
            state.currentPlanet
                ?.targetAltitude
                ?.coerceAtLeast(1f)
                ?: 1f

        planetName =
            state.currentPlanet
                ?.name
                ?: "CEL"
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        if (
            phase !=
            GameState.GamePhase.IN_FLIGHT
        ) {
            return
        }

        val panelWidth =
            width.toFloat()

        val panelHeight =
            height.toFloat()

        drawNavigationPanel(
            canvas,
            panelWidth,
            panelHeight
        )

        drawRadar(
            canvas,
            panelWidth,
            panelHeight
        )

        drawProgress(
            canvas,
            panelWidth,
            panelHeight
        )
    }

    private fun drawNavigationPanel(
        canvas: Canvas,
        panelWidth: Float,
        panelHeight: Float
    ) {

        val left =
            14f

        val right =
            panelWidth - 14f

        val top =
            14f

        val bottom =
            panelHeight - 14f

        panelPaint.setShadowLayer(
            12f,
            0f,
            3f,
            Color.BLACK
        )

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            20f,
            20f,
            panelPaint
        )

        panelPaint.clearShadowLayer()

        borderPaint.color =
            Color.rgb(
                50,
                190,
                255
            )

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            20f,
            20f,
            borderPaint
        )

        titlePaint.textAlign =
            Paint.Align.LEFT

        titlePaint.textSize =
            20f

        titlePaint.color =
            Color.rgb(
                100,
                220,
                255
            )

        canvas.drawText(
            "🛰️ LOT MIĘDZYPLANETARNY",
            28f,
            42f,
            titlePaint
        )

        smallPaint.textAlign =
            Paint.Align.LEFT

        smallPaint.textSize =
            14f

        smallPaint.color =
            Color.rgb(
                170,
                195,
                220
            )

        canvas.drawText(
            "CEL: $planetName",
            28f,
            68f,
            smallPaint
        )

        textPaint.textAlign =
            Paint.Align.LEFT

        textPaint.textSize =
            18f

        textPaint.color =
            Color.WHITE

        canvas.drawText(
            "🌍 ${altitude.toInt()} km",
            28f,
            98f,
            textPaint
        )

        canvas.drawText(
            "💨 ${velocity.toInt()} km/h",
            28f,
            124f,
            textPaint
        )

        textPaint.textAlign =
            Paint.Align.RIGHT

        textPaint.textSize =
            17f

        textPaint.color =
            Color.rgb(
                80,
                255,
                160
            )

        canvas.drawText(
            "🤖 AUTOPILOT",
            panelWidth - 28f,
            98f,
            textPaint
        )

        textPaint.textSize =
            14f

        textPaint.color =
            Color.rgb(
                150,
                230,
                190
            )

        canvas.drawText(
            "NAWIGACJA AKTYWNA",
            panelWidth - 28f,
            122f,
            textPaint
        )
    }

    private fun drawRadar(
        canvas: Canvas,
        panelWidth: Float,
        panelHeight: Float
    ) {

        val centerX =
            panelWidth / 2f

        val centerY =
            panelHeight * 0.56f

        val radius =
            minOf(
                panelWidth * 0.32f,
                105f
            )

        radarPaint.style =
            Paint.Style.STROKE

        radarPaint.strokeWidth =
            2f

        radarPaint.color =
            Color.argb(
                120,
                70,
                200,
                255
            )

        canvas.drawCircle(
            centerX,
            centerY,
            radius,
            radarPaint
        )

        canvas.drawCircle(
            centerX,
            centerY,
            radius * 0.66f,
            radarPaint
        )

        canvas.drawCircle(
            centerX,
            centerY,
            radius * 0.33f,
            radarPaint
        )

        canvas.drawLine(
            centerX - radius,
            centerY,
            centerX + radius,
            centerY,
            radarPaint
        )

        canvas.drawLine(
            centerX,
            centerY - radius,
            centerX,
            centerY + radius,
            radarPaint
        )

        val sweepAngle =
            animationTime * 2.4f

        val sweepX =
            centerX +
                cos(sweepAngle) *
                radius

        val sweepY =
            centerY +
                sin(sweepAngle) *
                radius

        radarPaint.color =
            Color.rgb(
                70,
                255,
                170
            )

        radarPaint.strokeWidth =
            4f

        canvas.drawLine(
            centerX,
            centerY,
            sweepX,
            sweepY,
            radarPaint
        )

        radarPaint.style =
            Paint.Style.FILL

        radarPaint.color =
            Color.rgb(
                80,
                255,
                170
            )

        canvas.drawCircle(
            centerX,
            centerY,
            7f,
            radarPaint
        )

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            13f

        textPaint.color =
            Color.rgb(
                130,
                220,
                255
            )

        canvas.drawText(
            "N",
            centerX,
            centerY - radius - 8f,
            textPaint
        )

        canvas.drawText(
            "S",
            centerX,
            centerY + radius + 18f,
            textPaint
        )

        canvas.drawText(
            "W",
            centerX - radius - 12f,
            centerY + 5f,
            textPaint
        )

        canvas.drawText(
            "E",
            centerX + radius + 12f,
            centerY + 5f,
            textPaint
        )
    }

    private fun drawProgress(
        canvas: Canvas,
        panelWidth: Float,
        panelHeight: Float
    ) {

        val left =
            28f

        val right =
            panelWidth - 28f

        val top =
            panelHeight - 65f

        val bottom =
            top + 12f

        val progress =
            (
                altitude /
                    targetAltitude
                ).coerceIn(
                    0f,
                    1f
                )

        radarPaint.style =
            Paint.Style.FILL

        radarPaint.color =
            Color.rgb(
                30,
                55,
                75
            )

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            6f,
            6f,
            radarPaint
        )

        progressPaint.color =
            Color.rgb(
                60,
                210,
                255
            )

        canvas.drawRoundRect(
            left,
            top,
            left +
                (
                    right - left
                    ) *
                    progress,
            bottom,
            6f,
            6f,
            progressPaint
        )

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            13f

        textPaint.color =
            Color.WHITE

        canvas.drawText(
            "POSTĘP MISJI ${(
                progress * 100f
                ).toInt()}%",
            panelWidth / 2f,
            top - 8f,
            textPaint
        )

        textPaint.textSize =
            12f

        textPaint.color =
            Color.rgb(
                150,
                200,
                225
            )

        canvas.drawText(
            "TRASA → $planetName",
            panelWidth / 2f,
            bottom + 25f,
            textPaint
        )
    }

    override fun onDetachedFromWindow() {

        handler.removeCallbacks(
            updateRunnable
        )

        super.onDetachedFromWindow()
    }
}
