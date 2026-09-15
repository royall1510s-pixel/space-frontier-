package com.spacefrontier.game.ui

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

    private val particlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val separatorPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var phase:
            GameState.GamePhase? = null

    private var previousStage =
        0

    private var stage =
        0

    private var animationTime =
        0f

    private var separationAnimation =
        0f

    private var showSeparation =
        false

    private val random =
        Random(System.currentTimeMillis())

    private val updateRunnable =
        object : Runnable {

            override fun run() {

                readGameState()

                animationTime +=
                    0.12f

                if (showSeparation) {

                    separationAnimation +=
                        0.08f

                    if (
                        separationAnimation >=
                        1.0f
                    ) {

                        showSeparation =
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

        separatorPaint.typeface =
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

    private fun readGameState() {

        val engine =
            getGameEngine()
                ?: return

        val state =
            engine.getGameState()

        phase =
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

            showSeparation =
                true

            separationAnimation =
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

        drawStageIndicator(
            canvas,
            centerX
        )

        drawEngineGlow(
            canvas,
            centerX,
            rocketBottom,
            power
        )

        drawFlame(
            canvas,
            centerX,
            rocketBottom,
            power
        )

        drawParticles(
            canvas,
            centerX,
            rocketBottom,
            power
        )

        if (stage >= 3) {

            drawOverloadGlow(
                canvas,
                centerX,
                rocketBottom
            )
        }

        if (showSeparation) {

            drawStageSeparation(
                canvas,
                centerX,
                rocketBottom
            )
        }
    }

    private fun drawStageIndicator(
        canvas: Canvas,
        centerX: Float
    ) {

        val stageText =
            when (stage) {

                1 ->
                    "STAGE 1  •  START"

                2 ->
                    "STAGE 2  •  BOOST"

                else ->
                    "STAGE 3  •  MAX THRUST"
            }

        val powerText =
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
                        80
                    )

                else ->
                    Color.rgb(
                        100,
                        210,
                        255
                    )
            }

        canvas.drawText(
            stageText,
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
            powerText,
            centerX,
            53f,
            textPaint
        )

        textPaint.textAlign =
            Paint.Align.LEFT
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
                        180,
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
                    animationTime * 8.0
                ) *
                    0.10f +
                    1f
                ).toFloat()

        val flameLength =
            65f *
                    power *
                    pulse

        val flameWidth =
            18f *
                    power

        flamePaint.color =
            when (stage) {

                1 ->
                    Color.rgb(
                        255,
                        125,
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
                        60,
                        10
                    )
            }

        val flamePath =
            android.graphics.Path()

        flamePath.moveTo(
            centerX -
                    flameWidth,
            centerY
        )

        flamePath.cubicTo(
            centerX -
                    flameWidth * 0.8f,
            centerY +
                    flameLength * 0.35f,

            centerX -
                    flameWidth * 0.55f,
            center
