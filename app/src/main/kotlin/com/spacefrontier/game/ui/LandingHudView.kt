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

class LandingHudView(
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

    private val infoPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val speedPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var phase:
        GameState.GamePhase? = null

    private var velocity =
        0f

    private var altitude =
        0f

    private var animationTime =
        0f

    private val updateRunnable =
        object : Runnable {

            override fun run() {

                updateState()

                animationTime +=
                    0.10f

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

        titlePaint.typeface =
            Typeface.DEFAULT_BOLD

        infoPaint.typeface =
            Typeface.DEFAULT_BOLD

        speedPaint.typeface =
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

    private fun updateState() {

        val engine =
            getGameEngine()
                ?: return

        val state =
            engine.getGameState()

        phase =
            state.gamePhase

        velocity =
            state.rocket.velocity

        altitude =
            state.rocket.altitude
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        val currentPhase =
            phase

        if (
            currentPhase !=
            GameState.GamePhase.LANDING_SEQUENCE &&
            currentPhase !=
            GameState.GamePhase.LANDED_SUCCESS &&
            currentPhase !=
            GameState.GamePhase.LANDED_FAILED
        ) {

            return
        }

        val centerX =
            width / 2f

        val centerY =
            height / 2f

        when (currentPhase) {

            GameState.GamePhase.LANDING_SEQUENCE ->
                drawLanding(
                    canvas,
                    centerX,
                    centerY
                )

            GameState.GamePhase.LANDED_SUCCESS ->
                drawSuccess(
                    canvas,
                    centerX,
                    centerY
                )

            GameState.GamePhase.LANDED_FAILED ->
                drawFailure(
                    canvas,
                    centerX,
                    centerY
                )

            else -> Unit
        }
    }

    private fun drawLanding(
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

        panelPaint.color =
            Color.argb(
                225,
                5,
                25,
                45
            )

        canvas.drawRoundRect(
            20f,
            centerY - 85f,
            width - 20f,
            centerY + 85f,
            22f,
            22f,
            panelPaint
        )

        borderPaint.color =
            Color.rgb(
                80,
                190,
                255
            )

        canvas.drawRoundRect(
            20f,
            centerY - 85f,
            width - 20f,
            centerY + 85f,
            22f,
            22f,
            borderPaint
        )

        titlePaint.textAlign =
            Paint.Align.CENTER

        titlePaint.textSize =
            27f

        titlePaint.color =
            Color.rgb(
                100,
                210,
                255
            )

        canvas.drawText(
            "LĄDOWANIE",
            centerX,
            centerY - 42f,
            titlePaint
        )

        infoPaint.textAlign =
            Paint.Align.CENTER

        infoPaint.textSize =
            16f

        infoPaint.color =
            Color.WHITE

        canvas.drawText(
            "Wysokość: ${altitude.toInt()} km",
            centerX,
            centerY - 10f,
            infoPaint
        )

        val safe =
            velocity <= 45f

        speedPaint.textSize =
            21f

        speedPaint.color =
            if (safe) {

                Color.rgb(
                    80,
                    255,
                    160
                )

            } else {

                Color.rgb(
                    255,
                    70,
                    70
                )
            }

        canvas.drawText(
            "Prędkość: ${velocity.toInt()} km/h",
            centerX,
            centerY + 23f,
            speedPaint
        )

        infoPaint.textSize =
            14f

        infoPaint.color =
            Color.argb(
                (
                    150f +
                        pulse * 105f
                ).toInt(),
                255,
                255,
                255
            )

        canvas.drawText(
            "AUTOMATYCZNA PROCEDURA LĄDOWANIA",
            centerX,
            centerY + 53f,
            infoPaint
        )

        infoPaint.textAlign =
            Paint.Align.LEFT
    }

    private fun drawSuccess(
        canvas: Canvas,
        centerX: Float,
        centerY: Float
    ) {

        panelPaint.color =
            Color.argb(
                235,
                5,
                50,
                30
            )

        canvas.drawRoundRect(
            20f,
            centerY - 90f,
            width - 20f,
            centerY + 90f,
            22f,
            22f,
            panelPaint
        )

        borderPaint.color =
            Color.rgb(
                70,
                255,
                150
            )

        canvas.drawRoundRect(
            20f,
            centerY - 90f,
            width - 20f,
            centerY + 90f,
            22f,
            22f,
            borderPaint
        )

        titlePaint.textAlign =
            Paint.Align.CENTER

        titlePaint.textSize =
            29f

        titlePaint.color =
            Color.rgb(
                80,
                255,
                150
            )

        canvas.drawText(
            "LĄDOWANIE UDANE!",
            centerX,
            centerY - 42f,
            titlePaint
        )

        infoPaint.textAlign =
            Paint.Align.CENTER

        infoPaint.textSize =
            18f

        infoPaint.color =
            Color.WHITE

        canvas.drawText(
            "Rakieta bezpiecznie dotarła",
            centerX,
            centerY - 5f,
            infoPaint
        )

        canvas.drawText(
            "na powierzchnię planety.",
            centerX,
            centerY + 22f,
            infoPaint
        )

        infoPaint.textSize =
            15f

        infoPaint.color =
            Color.rgb(
                190,
                230,
                210
            )

        canvas.drawText(
            "MISJA ZAKOŃCZONA",
            centerX,
            centerY + 58f,
            infoPaint
        )

        infoPaint.textAlign =
            Paint.Align.LEFT
    }

    private fun drawFailure(
        canvas: Canvas,
        centerX: Float,
        centerY: Float
    ) {

        panelPaint.color =
            Color.argb(
                235,
                65,
                10,
                15
            )

        canvas.drawRoundRect(
            20f,
            centerY - 90f,
            width - 20f,
            centerY + 90f,
            22f,
            22f,
            panelPaint
        )

        borderPaint.color =
            Color.rgb(
                255,
                70,
                70
            )

        canvas.drawRoundRect(
            20f,
            centerY - 90f,
            width - 20f,
            centerY + 90f,
            22f,
            22f,
            borderPaint
        )

        titlePaint.textAlign =
            Paint.Align.CENTER

        titlePaint.textSize =
            29f

        titlePaint.color =
            Color.rgb(
                255,
                80,
                80
            )

        canvas.drawText(
            "LĄDOWANIE NIEUDANE",
            centerX,
            centerY - 42f,
            titlePaint
        )

        infoPaint.textAlign =
            Paint.Align.CENTER

        infoPaint.textSize =
            18f

        infoPaint.color =
            Color.WHITE

        canvas.drawText(
            "Rakieta nie utrzymała",
            centerX,
            centerY - 5f,
            infoPaint
        )

        canvas.drawText(
            "bezpiecznej prędkości.",
            centerX,
            centerY + 22f,
            infoPaint
        )

        infoPaint.textSize =
            15f

        infoPaint.color =
            Color.rgb(
                255,
                190,
                190
            )

        canvas.drawText(
            "MISJA ZAKOŃCZONA",
            centerX,
            centerY + 58f,
            infoPaint
        )

        infoPaint.textAlign =
            Paint.Align.LEFT
    }

    override fun onDetachedFromWindow() {

        handler.removeCallbacks(
            updateRunnable
        )

        super.onDetachedFromWindow()
    }
}
