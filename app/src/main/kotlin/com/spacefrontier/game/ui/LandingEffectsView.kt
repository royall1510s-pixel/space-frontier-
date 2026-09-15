package com.spacefrontier.game.ui

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.View
import com.spacefrontier.game.logic.FlightController
import com.spacefrontier.game.logic.GameEngine
import com.spacefrontier.game.models.GameState
import kotlin.math.abs
import kotlin.math.sin

class LandingEffectsView(
    context: Context
) : View(context) {

    private val handler =
        Handler(Looper.getMainLooper())

    private val glowPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val parachutePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val parachuteLinePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val zonePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val zoneBorderPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val markerPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val path =
        Path()

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
                    0.08f

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

        textPaint.typeface =
            Typeface.DEFAULT_BOLD

        parachuteLinePaint.style =
            Paint.Style.STROKE

        parachuteLinePaint.strokeWidth =
            3f

        zoneBorderPaint.style =
            Paint.Style.STROKE

        zoneBorderPaint.strokeWidth =
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

        altitude =
            state.rocket.altitude

        velocity =
            state.rocket.velocity
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        when (phase) {

            GameState.GamePhase.LANDING_SEQUENCE -> {

                drawLandingEffects(
                    canvas
                )
            }

            GameState.GamePhase.LANDED_SUCCESS -> {

                drawSuccessEffect(
                    canvas
                )
            }

            GameState.GamePhase.LANDED_FAILED -> {

                drawFailureEffect(
                    canvas
                )
            }

            else -> Unit
        }
    }

    private fun drawLandingEffects(
        canvas: Canvas
    ) {

        val centerX =
            width / 2f

        val centerY =
            height / 2f

        val safeVelocity =
            FlightController
                .landingVelocityLimit(
                    altitude
                )

        val currentVelocity =
            abs(
                velocity
            )

        val safe =
            currentVelocity <=
                    safeVelocity

        val pulse =
            (
                sin(
                    animationTime * 8f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        /*
         * POŚWIATA RAKIETY
         */

        glowPaint.color =
            if (safe) {

                Color.argb(
                    (
                        35f +
                            pulse * 35f
                        ).toInt(),
                    50,
                    220,
                    255
                )

            } else {

                Color.argb(
                    (
                        45f +
                            pulse * 55f
                        ).toInt(),
                    255,
                    50,
                    50
                )
            }

        glowPaint.setShadowLayer(
            45f,
            0f,
            0f,
            glowPaint.color
        )

        canvas.drawCircle(
            centerX,
            centerY - 25f,
            55f,
            glowPaint
        )

        glowPaint.clearShadowLayer()

        /*
         * STREFA LĄDOWANIA
         */

        val groundY =
            height - 45f

        val zoneLeft =
            centerX - 115f

        val zoneRight =
            centerX + 115f

        zonePaint.color =
            Color.argb(
                115,
                35,
                210,
                100
            )

        canvas.drawRoundRect(
            zoneLeft,
            groundY - 14f,
            zoneRight,
            groundY + 14f,
            14f,
            14f,
            zonePaint
        )

        zoneBorderPaint.color =
            Color.rgb(
                70,
                255,
                150
            )

        canvas.drawRoundRect(
            zoneLeft,
            groundY - 14f,
            zoneRight,
            groundY + 14f,
            14f,
            14f,
            zoneBorderPaint
        )

        /*
         * PUNKT CELU
         */

        markerPaint.color =
            Color.WHITE

        canvas.drawCircle(
            centerX,
            groundY,
            5f,
            markerPaint
        )

        /*
         * LINIE PROWADZĄCE
         */

        markerPaint.color =
            Color.argb(
                100,
                100,
                220,
                255
            )

        markerPaint.strokeWidth =
            2f

        canvas.drawLine(
            centerX - 70f,
            centerY + 30f,
            centerX - 30f,
            groundY - 18f,
            markerPaint
        )

        canvas.drawLine(
            centerX + 70f,
            centerY + 30f,
            centerX + 30f,
            groundY - 18f,
            markerPaint
        )

        /*
         * SPADOCHRON
         */

        if (
            altitude <= 250f
        ) {

            drawParachute(
                canvas,
                centerX,
                centerY - 105f
            )
        }

        /*
         * PYŁ PRZY ZIEMI
         */

        if (
            altitude <= 120f
        ) {

            drawLandingDust(
                canvas,
                centerX,
                groundY
            )
        }

        /*
         * NAPIS STREFY
         */

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            13f

        textPaint.color =
            Color.rgb(
                110,
                255,
                170
            )

        canvas.drawText(
            "STREFA LĄDOWANIA",
            centerX,
            groundY + 38f,
            textPaint
        )

        /*
         * OSTRZEŻENIE
         */

        if (!safe) {

            val alpha =
                (
                    130f +
                        pulse * 125f
                    ).toInt()

            textPaint.color =
                Color.argb(
                    alpha,
                    255,
                    70,
                    70
                )

            textPaint.textSize =
                15f

            canvas.drawText(
                "REDUKCJA PRĘDKOŚCI",
                centerX,
                38f,
                textPaint
            )
        }
    }

    private fun drawParachute(
        canvas: Canvas,
        centerX: Float,
        centerY: Float
    ) {

        val opening =
            (
                sin(
                    animationTime * 4f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        val canopyWidth =
            105f +
                opening * 8f

        val canopyHeight =
            50f

        /*
         * CZASZA
         */

        parachutePaint.color =
            Color.argb(
                225,
                235,
                235,
                245
            )

        path.reset()

        path.moveTo(
            centerX - canopyWidth,
            centerY
        )

        path.quadTo(
            centerX,
            centerY - canopyHeight,
            centerX + canopyWidth,
            centerY
        )

        path.close()

        canvas.drawPath(
            path,
            parachutePaint
        )

        /*
         * PODZIAŁ CZASZY
         */

        parachuteLinePaint.color =
            Color.rgb(
                110,
                130,
                150
            )

        canvas.drawLine(
            centerX,
            centerY - canopyHeight,
            centerX,
            centerY,
            parachuteLinePaint
        )

        canvas.drawLine(
            centerX - canopyWidth * 0.5f,
            centerY - canopyHeight * 0.5f,
            centerX - canopyWidth * 0.25f,
            centerY,
            parachuteLinePaint
        )

        canvas.drawLine(
            centerX + canopyWidth * 0.5f,
            centerY - canopyHeight * 0.5f,
            centerX + canopyWidth * 0.25f,
            centerY,
            parachuteLinePaint
        )

        /*
         * LINKI
         */

        canvas.drawLine(
            centerX - canopyWidth * 0.75f,
            centerY,
            centerX - 18f,
            centerY + 70f,
            parachuteLinePaint
        )

        canvas.drawLine(
            centerX + canopyWidth * 0.75f,
            centerY,
            centerX + 18f,
            centerY + 70f,
            parachuteLinePaint
        )

        /*
         * NAPIS
         */

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            12f

        textPaint.color =
            Color.rgb(
                230,
                240,
                255
            )

        canvas.drawText(
            "SPADOCHRON",
            centerX,
            centerY - canopyHeight - 10f,
            textPaint
        )
    }

    private fun drawLandingDust(
        canvas: Canvas,
        centerX: Float,
        groundY: Float
    ) {

        val movement =
            (
                animationTime * 45f
            ) % 80f

        val particlePaint =
            Paint(
                Paint.ANTI_ALIAS_FLAG
            )

        particlePaint.color =
            Color.argb(
                130,
                180,
                190,
                195
            )

        val positions =
            floatArrayOf(
                -70f,
                -42f,
                -15f,
                15f,
                42f,
                70f
            )

        for (
            index in positions.indices
        ) {

            val direction =
                if (
                    index % 2 == 0
                ) {
                    1f
                } else {
                    -1f
                }

            val x =
                centerX +
                    positions[index] +
                    movement *
                    direction

            val y =
                groundY -
                    5f -
                    (
                        (
                            index * 7f +
                                animationTime * 30f
                        ) % 25f
                    )

            val radius =
                2f +
                    (
                        index % 3
                    )

            canvas.drawCircle(
                x,
                y,
                radius,
                particlePaint
            )
        }
    }

    private fun drawSuccessEffect(
        canvas: Canvas
    ) {

        val centerX =
            width / 2f

        val centerY =
            height / 2f

        val pulse =
            (
                sin(
                    animationTime * 5f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        glowPaint.color =
            Color.argb(
                (
                    25f +
                        pulse * 50f
                    ).toInt(),
                60,
                255,
                140
            )

        glowPaint.setShadowLayer(
            70f,
            0f,
            0f,
            glowPaint.color
        )

        canvas.drawCircle(
            centerX,
            centerY,
            45f + pulse * 25f,
            glowPaint
        )

        glowPaint.clearShadowLayer()

        val ringPaint =
            Paint(
                Paint.ANTI_ALIAS_FLAG
            )

        ringPaint.style =
            Paint.Style.STROKE

        ringPaint.strokeWidth =
            5f

        ringPaint.color =
            Color.argb(
                (
                    100f +
                        pulse * 155f
                    ).toInt(),
                70,
                255,
                150
            )

        canvas.drawCircle(
            centerX,
            centerY,
            75f + pulse * 20f,
            ringPaint
        )
    }

    private fun drawFailureEffect(
        canvas: Canvas
    ) {

        val centerX =
            width / 2f

        val centerY =
            height / 2f

        val pulse =
            (
                sin(
                    animationTime * 9f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        val ringPaint =
            Paint(
                Paint.ANTI_ALIAS_FLAG
            )

        ringPaint.style =
            Paint.Style.STROKE

        ringPaint.strokeWidth =
            6f

        ringPaint.color =
            Color.argb(
                (
                    100f +
                        pulse * 155f
                    ).toInt(),
                255,
                50,
                50
            )

        canvas.drawCircle(
            centerX,
            centerY,
            65f + pulse * 30f,
            ringPaint
        )

        glowPaint.color =
            Color.argb(
                (
                    20f +
                        pulse * 45f
                    ).toInt(),
                255,
                40,
                40
            )

        glowPaint.setShadowLayer(
            65f,
            0f,
            0f,
            glowPaint.color
        )

        canvas.drawCircle(
            centerX,
            centerY,
            40f,
            glowPaint
        )

        glowPaint.clearShadowLayer()
    }

    override fun onDetachedFromWindow() {

        handler.removeCallbacks(
            updateRunnable
        )

        super.onDetachedFromWindow()
    }
}
