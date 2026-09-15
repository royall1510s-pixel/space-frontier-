package com.spacefrontier.game.ui

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.view.View
import com.spacefrontier.game.logic.GameEngine
import com.spacefrontier.game.models.GameState
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

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

    private val smokePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var phase:
            GameState.GamePhase? = null

    private var animationTime =
        0f

    private var stage =
        0

    private val random =
        Random(System.currentTimeMillis())

    private val updateRunnable =
        object : Runnable {

            override fun run() {

                readGameState()

                animationTime +=
                    0.12f

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

    private fun readGameState() {

        val engine =
            getGameEngine()
                ?: return

        val state =
            engine.getGameState()

        phase =
            state.gamePhase

        stage =
            when (state.gamePhase) {

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
            currentPhase ==
            GameState.GamePhase.LANDING_SEQUENCE ||
            currentPhase ==
            GameState.GamePhase.LANDED_SUCCESS ||
            currentPhase ==
            GameState.GamePhase.LANDED_FAILED
        ) {

            return
        }

        if (stage <= 0) {

            return
        }

        if (
            currentPhase !=
            GameState.GamePhase.STAGE_1_ACTIVE &&
            currentPhase !=
            GameState.GamePhase.STAGE_2_ACTIVE &&
            currentPhase !=
            GameState.GamePhase.STAGE_3_ACTIVE &&
            currentPhase !=
            GameState.GamePhase.IN_FLIGHT
        ) {

            return
        }

        val centerX =
            width / 2f

        val rocketBottom =
            height * 0.72f

        val power =
            when (stage) {

                1 -> 1.0f

                2 -> 1.45f

                else -> 2.0f
            }

        drawEngineGlow(
            canvas = canvas,
            centerX = centerX,
            centerY = rocketBottom,
            power = power
        )

        drawFlame(
            canvas = canvas,
            centerX = centerX,
            centerY = rocketBottom,
            power = power
        )

        if (stage >= 2) {

            drawParticles(
                canvas = canvas,
                centerX = centerX,
                centerY = rocketBottom,
                power = power
            )
        }

        if (stage >= 3) {

            drawOverloadGlow(
                canvas = canvas,
                centerX = centerX,
                centerY = rocketBottom
            )
        }
    }

    private fun drawEngineGlow(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        power: Float
    ) {

        val pulse =
            (
                sin(
                    animationTime * 5.0
                ) *
                    0.12f +
                    0.88f
                ).toFloat()

        val radius =
            45f *
                    power *
                    pulse

        glowPaint.shader =
            RadialGradient(
                centerX,
                centerY,
                radius,
                intArrayOf(
                    Color.argb(
                        170,
                        255,
                        120,
                        20
                    ),
                    Color.argb(
                        80,
                        255,
                        60,
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
                    animationTime * 8.0
                ) *
                    0.10f +
                    1f
                ).toFloat()

        val flameLength =
            65f *
                    power *
                    pulse

        val width =
            18f *
                    power

        val top =
            centerY

        val bottom =
            centerY +
                    flameLength

        flamePaint.color =
            Color.argb(
                210,
                255,
                120,
                20
            )

        flamePaint.style =
            Paint.Style.FILL

        val flamePath =
            android.graphics.Path()

        flamePath.moveTo(
            centerX - width,
            top
        )

        flamePath.cubicTo(
            centerX -
                    width * 0.8f,
            top +
                    flameLength * 0.35f,

            centerX -
                    width * 0.55f,
            bottom -
                    flameLength * 0.15f,

            centerX,
            bottom
        )

        flamePath.cubicTo(
            centerX +
                    width * 0.55f,
            bottom -
                    flameLength * 0.15f,

            centerX +
                    width * 0.8f,
            top +
                    flameLength * 0.35f,

            centerX + width,
            top
        )

        flamePath.close()

        canvas.drawPath(
            flamePath,
            flamePaint
        )

        val coreLength =
            flameLength *
                    0.65f

        corePaint.color =
            Color.argb(
                245,
                255,
                235,
                150
            )

        val corePath =
            android.graphics.Path()

        corePath.moveTo(
            centerX -
                    width * 0.42f,
            top
        )

        corePath.cubicTo(
            centerX -
                    width * 0.28f,
            top +
                    coreLength * 0.35f,

            centerX -
                    width * 0.18f,
            top +
                    coreLength * 0.75f,

            centerX,
            top +
                    coreLength
        )

        corePath.cubicTo(
            centerX +
                    width * 0.18f,
            top +
                    coreLength * 0.75f,

            centerX +
                    width * 0.28f,
            top +
                    coreLength * 0.35f,

            centerX +
                    width * 0.42f,
            top
        )

        corePath.close()

        canvas.drawPath(
            corePath,
            corePaint
        )
    }

    private fun drawParticles(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        power: Float
    ) {

        smokePaint.color =
            Color.argb(
                110,
                180,
                200,
                220
            )

        repeat(
            (8 * power).toInt()
        ) {

            val seed =
                animationTime *
                        7f +
                        it * 1.7f

            val angle =
                seed +
                        random.nextFloat() *
                        1.5f

            val distance =
                25f +
                        (
                            sin(
                                seed * 2f
                            ) + 1f
                        ) *
                        35f *
                        power

            val x =
                centerX +
                        cos(angle) *
                        distance

            val y =
                centerY +
                        40f +
                        (
                            (
                                animationTime * 90f +
                                it * 25f
                            ) %
                                (110f * power)
                            )

            val radius =
                2f +
                        random.nextFloat() *
                        4f

            canvas.drawCircle(
                x.toFloat(),
                y,
                radius,
                smokePaint
            )
        }
    }

    private fun drawOverloadGlow(
        canvas: Canvas,
        centerX: Float,
        centerY: Float
    ) {

        val pulse =
            (
                sin(
                    animationTime * 14.0
                ) *
                    0.5f +
                    0.5f
                ).toFloat()

        val radius =
            70f +
                    pulse * 25f

        glowPaint.shader =
            RadialGradient(
                centerX,
                centerY,
                radius,
                intArrayOf(
                    Color.argb(
                        120,
                        80,
                        180,
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

    override fun onDetachedFromWindow() {

        handler.removeCallbacks(
            updateRunnable
        )

        super.onDetachedFromWindow()
    }
}
