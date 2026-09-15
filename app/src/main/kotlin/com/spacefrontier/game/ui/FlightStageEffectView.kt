package com.spacefrontier.game.ui

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.View
import com.spacefrontier.game.logic.GameEngine
import com.spacefrontier.game.models.GameState
import kotlin.math.cos
import kotlin.math.sin

class FlightStageEffectView(
    context: Context
) : View(context) {

    private val handler =
        Handler(Looper.getMainLooper())

    private val glowPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val flamePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val corePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val particlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val smokePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val separationPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var gamePhase:
        GameState.GamePhase? = null

    private var stage =
        0

    private var previousStage =
        0

    private var animationTime =
        0f

    private var separationTime =
        0f

    private var separationVisible =
        false

    private val updateRunnable =
        object : Runnable {

            override fun run() {

                updateGameState()

                animationTime +=
                    0.10f

                if (
                    separationVisible
                ) {

                    separationTime +=
                        0.10f

                    if (
                        separationTime >=
                        1f
                    ) {

                        separationVisible =
                            false
                    }
                }

                invalidate()

                handler.postDelayed(
                    this,
                    40
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

        separationPaint.typeface =
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

    private fun updateGameState() {

        val engine =
            getGameEngine()
                ?: return

        val state =
            engine.getGameState()

        gamePhase =
            state.gamePhase

        val newStage =
            when (
                state.gamePhase
            ) {

                GameState.GamePhase.STAGE_1_ACTIVE ->
                    1

                GameState.GamePhase.STAGE_2_ACTIVE ->
                    2

                GameState.GamePhase.STAGE_3_ACTIVE ->
                    3

                GameState.GamePhase.IN_FLIGHT ->
                    3

                else ->
                    0
            }

        if (
            newStage > previousStage &&
            previousStage > 0
        ) {

            separationVisible =
                true

            separationTime =
                0f
        }

        if (
            newStage > 0
        ) {

            previousStage =
                newStage
        }

        stage =
            newStage
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        if (
            stage <= 0
        ) {
            return
        }

        if (
            gamePhase ==
            GameState.GamePhase.LANDING_SEQUENCE ||
            gamePhase ==
            GameState.GamePhase.LANDED_SUCCESS ||
            gamePhase ==
            GameState.GamePhase.LANDED_FAILED
        ) {
            return
        }

        val centerX =
            width / 2f

        val engineY =
            height * 0.72f

        val power =
            when (stage) {

                1 ->
                    1.0f

                2 ->
                    1.45f

                else ->
                    2.0f
            }

        canvas.save()

        drawEngineShake(
            canvas
        )

        drawSpeedLines(
            canvas,
            centerX
        )

        drawStageLabel(
            canvas,
            centerX
        )

        drawGlow(
            canvas,
            centerX,
            engineY,
            power
        )

        drawFlame(
            canvas,
            centerX,
            engineY,
            power
        )

        drawSmoke(
            canvas,
            centerX,
            engineY,
            power
        )

        drawParticles(
            canvas,
            centerX,
            engineY,
            power
        )

        if (
            stage >= 3
        ) {

            drawMaximumThrustGlow(
                canvas,
                centerX,
                engineY
            )
        }

        if (
            separationVisible
        ) {

            drawStageChange(
                canvas,
                centerX,
                engineY
            )
        }

        canvas.restore()
    }

    private fun drawEngineShake(
        canvas: Canvas
    ) {

        if (
            stage <= 0
        ) {
            return
        }

        val intensity =
            when (stage) {

                1 ->
                    0.8f

                2 ->
                    1.6f

                else ->
                    2.8f
            }

        val x =
            sin(
                animationTime * 35f
            ) *
                intensity

        val y =
            cos(
                animationTime * 31f
            ) *
                intensity

        canvas.translate(
            x.toFloat(),
            y.toFloat()
        )
    }

    private fun drawSpeedLines(
        canvas: Canvas,
        centerX: Float
    ) {

        if (
            stage < 2
        ) {
            return
        }

        val count =
            if (stage == 2) {
                8
            } else {
                14
            }

        val linePaint =
            Paint(
                Paint.ANTI_ALIAS_FLAG
            )

        linePaint.color =
            Color.argb(
                90,
                130,
                210,
                255
            )

        linePaint.strokeWidth =
            2f

        for (
            index in 0 until count
        ) {

            val angle =
                animationTime * 1.8f +
                    index * 0.45f

            val side =
                sin(angle) * 130f

            val top =
                70f +
                    (
                        index * 37f
                    )

            val length =
                20f +
                    stage * 8f

            canvas.drawLine(
                centerX +
                    side.toFloat(),
                top,
                centerX +
                    side.toFloat(),
                top + length,
                linePaint
            )
        }
    }

    private fun drawStageLabel(
        canvas: Canvas,
        centerX: Float
    ) {

        val title =
            when (stage) {

                1 ->
                    "STAGE 1 • START"

                2 ->
                    "STAGE 2 • BOOST"

                else ->
                    "STAGE 3 • MAX THRUST"
            }

        val thrust =
            when (stage) {

                1 ->
                    "CIĄG 100%"

                2 ->
                    "CIĄG 145%"

                else ->
                    "CIĄG 200%"
            }

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            20f

        textPaint.color =
            when (stage) {

                1 ->
                    Color.WHITE

                2 ->
                    Color.rgb(
                        255,
                        210,
                        70
                    )

                else ->
                    Color.rgb(
                        100,
                        210,
                        255
                    )
            }

        canvas.drawText(
            title,
            centerX,
            34f,
            textPaint
        )

        textPaint.textSize =
            13f

        textPaint.color =
            Color.rgb(
                190,
                205,
                220
            )

        canvas.drawText(
            thrust,
            centerX,
            53f,
            textPaint
        )

        textPaint.textAlign =
            Paint.Align.LEFT
    }

    private fun drawGlow(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        power: Float
    ) {

        val pulse =
            (
                sin(
                    animationTime * 5f
                ) * 0.12f +
                    0.88f
            ).toFloat()

        val radius =
            42f *
                power *
                pulse

        glowPaint.shader =
            RadialGradient(
                centerX,
                centerY,
                radius,
                intArrayOf(
                    Color.argb(
                        190,
                        255,
                        150,
                        30
                    ),
                    Color.argb(
                        90,
                        255,
                        70,
                        10
                    ),
                    Color.TRANSPARENT
                ),
                floatArrayOf(
                    0f,
                    0.45f,
                    1f
                ),
                Shader.TileMode.CLAMP
            )

        canvas.drawCircle(
            centerX,
            centerY,
            radius,
            glowPaint
        )

        glowPaint.shader =
            null
    }

    private fun drawFlame(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        power: Float
    ) {

        val pulse =
            (
                sin(
                    animationTime * 8f
                ) * 0.10f +
                    1f
            ).toFloat()

        val length =
            60f *
                power *
                pulse

        val width =
            18f *
                power

        flamePaint.color =
            when (stage) {

                1 ->
                    Color.rgb(
                        255,
                        130,
                        20
                    )

                2 ->
                    Color.rgb(
                        255,
                        90,
                        15
                    )

                else ->
                    Color.rgb(
                        255,
                        55,
                        10
                    )
            }

        val path =
            Path()

        path.moveTo(
            centerX - width,
            centerY
        )

        path.cubicTo(
            centerX -
                width * 0.85f,
            centerY +
                length * 0.30f,

            centerX -
                width * 0.55f,
            centerY +
                length * 0.80f,

            centerX,
            centerY +
                length
        )

        path.cubicTo(
            centerX +
                width * 0.55f,
            centerY +
                length * 0.80f,

            centerX +
                width * 0.85f,
            centerY +
                length * 0.30f,

            centerX + width,
            centerY
        )

        path.close()

        canvas.drawPath(
            path,
            flamePaint
        )

        val coreLength =
            length * 0.62f

        corePaint.color =
            Color.rgb(
                255,
                245,
                170
            )

        val core =
            Path()

        core.moveTo(
            centerX -
                width * 0.40f,
            centerY
        )

        core.cubicTo(
            centerX -
                width * 0.25f,
            centerY +
                coreLength * 0.35f,

            centerX -
                width * 0.15f,
            centerY +
                coreLength * 0.75f,

            centerX,
            centerY +
                coreLength
        )

        core.cubicTo(
            centerX +
                width * 0.15f,
            centerY +
                coreLength * 0.75f,

            centerX +
                width * 0.25f,
            centerY +
                coreLength * 0.35f,

            centerX +
                width * 0.40f,
            centerY
        )

        core.close()

        canvas.drawPath(
            core,
            corePaint
        )
    }

    private fun drawSmoke(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        power: Float
    ) {

        smokePaint.color =
            Color.argb(
                70,
                180,
                195,
                210
            )

        val count =
            when (stage) {

                1 ->
                    4

                2 ->
                    7

                else ->
                    10
            }

        for (
            index in 0 until count
        ) {

            val movement =
                animationTime * 1.5f +
                    index * 0.9f

            val x =
                centerX +
                    sin(
                        movement
                    ) *
                    (
                        25f +
                            index * 5f
                    )

            val y =
                centerY +
                    40f +
                    (
                        (
                            animationTime *
                                45f +
                                index *
                                32f
                        ) %
                            (
                                130f *
                                    power
                            )
                    )

            val radius =
                5f +
                    index % 4

            canvas.drawCircle(
                x.toFloat(),
                y.toFloat(),
                radius.toFloat(),
                smokePaint
            )
        }
    }

    private fun drawParticles(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        power: Float
    ) {

        particlePaint.color =
            Color.argb(
                135,
                220,
                225,
                230
            )

        val count =
            when (stage) {

                1 ->
                    5

                2 ->
                    11

                else ->
                    18
            }

        for (
            index in 0 until count
        ) {

            val movement =
                animationTime * 7f +
                    index * 1.7f

            val spread =
                sin(
                    movement
                ) *
                    25f *
                    power

            val fall =
                (
                    animationTime *
                        100f +
                        index *
                        23f
                ) %
                    (
                        105f *
                            power
                    )

            val x =
                centerX +
                    spread

            val y =
                centerY +
                    25f +
                    fall

            val radius =
                2f +
                    index % 3

            canvas.drawCircle(
                x.toFloat(),
                y.toFloat(),
                radius.toFloat(),
                particlePaint
            )
        }
    }

    private fun drawMaximumThrustGlow(
        canvas: Canvas,
        centerX: Float,
        centerY: Float
    ) {

        val pulse =
            (
                sin(
                    animationTime * 14f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        val radius =
            60f +
                pulse * 28f

        glowPaint.shader =
            RadialGradient(
                centerX,
                centerY,
                radius,
                intArrayOf(
                    Color.argb(
                        130,
                        70,
                        190,
                        255
                    ),
                    Color.argb(
                        60,
                        30,
                        100,
                        255
                    ),
                    Color.TRANSPARENT
                ),
                floatArrayOf(
                    0f,
                    0.5f,
                    1f
                ),
                Shader.TileMode.CLAMP
            )

        canvas.drawCircle(
            centerX,
            centerY,
            radius,
            glowPaint
        )

        glowPaint.shader =
            null
    }

    private fun drawStageChange(
        canvas: Canvas,
        centerX: Float,
        centerY: Float
    ) {

        val progress =
            separationTime
                .coerceIn(
                    0f,
                    1f
                )

        val alpha =
            (
                (1f - progress) *
                    255f
            ).toInt()
                .coerceIn(
                    0,
                    255
                )

        val radius =
            35f +
                progress * 150f

        separationPaint.style =
            Paint.Style.STROKE

        separationPaint.strokeWidth =
            4f

        separationPaint.color =
            Color.argb(
                alpha,
                100,
                210,
                255
            )

        canvas.drawCircle(
            centerX,
            centerY,
            radius,
            separationPaint
        )

        separationPaint.style =
            Paint.Style.FILL

        separationPaint.textAlign =
            Paint.Align.CENTER

        separationPaint.textSize =
            22f

        separationPaint.color =
            Color.argb(
                alpha,
                255,
                255,
                255
            )

        canvas.drawText(
            "STAGE SEPARATION",
            centerX,
            centerY - 35f,
            separationPaint
        )

        separationPaint.textSize =
            14f

        canvas.drawText(
            "ZMIANA STOPNIA",
            centerX,
            centerY - 10f,
            separationPaint
        )

        separationPaint.textAlign =
            Paint.Align.LEFT
    }

    override fun onDetachedFromWindow() {

        handler.removeCallbacks(
            updateRunnable
        )

        super.onDetachedFromWindow()
    }
}
