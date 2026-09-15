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
import com.spacefrontier.game.logic.FlightController
import com.spacefrontier.game.logic.GameEngine
import com.spacefrontier.game.models.GameState
import kotlin.math.abs
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

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val smallPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val barBackgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val barSafePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val barDangerPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var phase:
        GameState.GamePhase? = null

    private var velocity =
        0f

    private var altitude =
        0f

    private var reward =
        0

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

        textPaint.typeface =
            Typeface.DEFAULT_BOLD

        smallPaint.typeface =
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

        reward =
            state.missionReward
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        val currentPhase =
            phase

        when (currentPhase) {

            GameState.GamePhase.LANDING_SEQUENCE -> {

                drawLanding(
                    canvas
                )
            }

            GameState.GamePhase.LANDED_SUCCESS -> {

                drawSuccess(
                    canvas
                )
            }

            GameState.GamePhase.LANDED_FAILED -> {

                drawFailure(
                    canvas
                )
            }

            else -> Unit
        }
    }

    private fun drawLanding(
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
                    animationTime * 7f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        /*
         * GŁÓWNY PANEL
         */

        panelPaint.color =
            Color.argb(
                235,
                4,
                18,
                35
            )

        canvas.drawRoundRect(
            15f,
            centerY - 175f,
            width - 15f,
            centerY + 175f,
            25f,
            25f,
            panelPaint
        )

        borderPaint.color =
            if (safe) {

                Color.rgb(
                    70,
                    210,
                    255
                )

            } else {

                Color.rgb(
                    255,
                    70,
                    70
                )
            }

        canvas.drawRoundRect(
            15f,
            centerY - 175f,
            width - 15f,
            centerY + 175f,
            25f,
            25f,
            borderPaint
        )

        /*
         * TYTUŁ
         */

        titlePaint.textAlign =
            Paint.Align.CENTER

        titlePaint.textSize =
            27f

        titlePaint.color =
            Color.rgb(
                100,
                215,
                255
            )

        canvas.drawText(
            "PROCEDURA LĄDOWANIA",
            centerX,
            centerY - 138f,
            titlePaint
        )

        /*
         * WYSOKOŚĆ
         */

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            18f

        textPaint.color =
            Color.WHITE

        canvas.drawText(
            "WYSOKOŚĆ: ${altitude.toInt()} km",
            centerX,
            centerY - 103f,
            textPaint
        )

        /*
         * CEL
         */

        smallPaint.textAlign =
            Paint.Align.CENTER

        smallPaint.textSize =
            14f

        smallPaint.color =
            Color.rgb(
                170,
                205,
                225
            )

        canvas.drawText(
            "CEL: 0 km",
            centerX,
            centerY - 78f,
            smallPaint
        )

        /*
         * PRĘDKOŚĆ
         */

        textPaint.textSize =
            24f

        textPaint.color =
            if (safe) {

                Color.rgb(
                    70,
                    255,
                    150
                )

            } else {

                Color.rgb(
                    255,
                    65,
                    65
                )
            }

        canvas.drawText(
            "PRĘDKOŚĆ: ${currentVelocity.toInt()} km/h",
            centerX,
            centerY - 38f,
            textPaint
        )

        /*
         * LIMIT
         */

        smallPaint.textSize =
            14f

        smallPaint.color =
            Color.WHITE

        canvas.drawText(
            "BEZPIECZNY LIMIT: ${safeVelocity.toInt()} km/h",
            centerX,
            centerY - 14f,
            smallPaint
        )

        /*
         * PASEK PRĘDKOŚCI
         */

        val barLeft =
            35f

        val barRight =
            width - 35f

        val barTop =
            centerY + 8f

        val barBottom =
            centerY + 28f

        barBackgroundPaint.color =
            Color.rgb(
                25,
                35,
                50
            )

        canvas.drawRoundRect(
            barLeft,
            barTop,
            barRight,
            barBottom,
            10f,
            10f,
            barBackgroundPaint
        )

        /*
         * Zielona strefa bezpieczeństwa
         */

        val safeRatio =
            (
                safeVelocity /
                        45f
                ).coerceIn(
                    0f,
                    1f
                )

        val safeRight =
            barLeft +
                    (
                        barRight -
                            barLeft
                        ) *
                    safeRatio

        barSafePaint.color =
            Color.rgb(
                40,
                190,
                100
            )

        canvas.drawRoundRect(
            barLeft,
            barTop,
            safeRight,
            barBottom,
            10f,
            10f,
            barSafePaint
        )

        /*
         * Czerwona strefa
         */

        if (
            safeRight <
            barRight
        ) {

            barDangerPaint.color =
                Color.rgb(
                    210,
                    45,
                    45
                )

            canvas.drawRect(
                safeRight,
                barTop,
                barRight,
                barBottom,
                barDangerPaint
            )
        }

        /*
         * WSKAŹNIK AKTUALNEJ PRĘDKOŚCI
         */

        val velocityRatio =
            (
                currentVelocity /
                        45f
                ).coerceIn(
                    0f,
                    1f
                )

        val indicatorX =
            barLeft +
                    (
                        barRight -
                            barLeft
                        ) *
                    velocityRatio

        val indicatorPaint =
            Paint(
                Paint.ANTI_ALIAS_FLAG
            )

        indicatorPaint.color =
            Color.WHITE

        canvas.drawCircle(
            indicatorX,
            (
                barTop +
                    barBottom
                ) / 2f,
            8f,
            indicatorPaint
        )

        /*
         * OPIS STREFY
         */

        smallPaint.textSize =
            12f

        smallPaint.color =
            Color.rgb(
                100,
                255,
                160
            )

        smallPaint.textAlign =
            Paint.Align.LEFT

        canvas.drawText(
            "BEZPIECZNIE",
            barLeft,
            centerY + 49f,
            smallPaint
        )

        smallPaint.textAlign =
            Paint.Align.RIGHT

        smallPaint.color =
            Color.rgb(
                255,
                100,
                100
            )

        canvas.drawText(
            "ZA SZYBKO",
            barRight,
            centerY + 49f,
            smallPaint
        )

        /*
         * SPADOCHRON
         */

        val parachuteOpened =
            altitude <= 250f

        smallPaint.textAlign =
            Paint.Align.CENTER

        smallPaint.textSize =
            16f

        smallPaint.color =
            if (parachuteOpened) {

                Color.rgb(
                    90,
                    255,
                    190
                )

            } else {

                Color.rgb(
                    160,
                    180,
                    200
                )
            }

        val parachuteText =
            if (parachuteOpened) {

                "SPADOCHRON OTWARTY"

            } else {

                "SPADOCHRON: OCZEKIWANIE"
            }

        canvas.drawText(
            parachuteText,
            centerX,
            centerY + 82f,
            smallPaint
        )

        /*
         * STATUS
         */

        titlePaint.textSize =
            18f

        titlePaint.color =
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

        val statusText =
            if (safe) {

                "PRĘDKOŚĆ BEZPIECZNA"

            } else {

                "REDUKUJ PRĘDKOŚĆ!"
            }

        canvas.drawText(
            statusText,
            centerX,
            centerY + 113f,
            titlePaint
        )

        /*
         * MIGAJĄCE OSTRZEŻENIE
         */

        if (!safe) {

            val alpha =
                (
                    150f +
                        pulse * 105f
                    ).toInt()

            smallPaint.color =
                Color.argb(
                    alpha,
                    255,
                    100,
                    100
                )

            smallPaint.textSize =
                14f

            canvas.drawText(
                "UWAGA — PRĘDKOŚĆ PRZEKRACZA LIMIT",
                centerX,
                centerY + 143f,
                smallPaint
            )
        } else {

            smallPaint.color =
                Color.rgb(
                    170,
                    210,
                    190
                )

            smallPaint.textSize =
                13f

            canvas.drawText(
                "AUTOMATYCZNE HAMOWANIE",
                centerX,
                centerY + 143f,
                smallPaint
            )
        }

        smallPaint.textAlign =
            Paint.Align.LEFT
    }

    private fun drawSuccess(
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

        panelPaint.color =
            Color.argb(
                240,
                4,
                55,
                30
            )

        canvas.drawRoundRect(
            15f,
            centerY - 155f,
            width - 15f,
            centerY + 155f,
            25f,
            25f,
            panelPaint
        )

        borderPaint.color =
            Color.rgb(
                70,
                255,
                150
            )

        canvas.drawRoundRect(
            15f,
            centerY - 155f,
            width - 15f,
            centerY + 155f,
            25f,
            25f,
            borderPaint
        )

        titlePaint.textAlign =
            Paint.Align.CENTER

        titlePaint.textSize =
            30f

        titlePaint.color =
            Color.rgb(
                80,
                255,
                150
            )

        canvas.drawText(
            "LĄDOWANIE UDANE!",
            centerX,
            centerY - 92f,
            titlePaint
        )

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            19f

        textPaint.color =
            Color.WHITE

        canvas.drawText(
            "RAKIETA BEZPIECZNIE",
            centerX,
            centerY - 48f,
            textPaint
        )

        canvas.drawText(
            "OSIĄGNĘŁA POWIERZCHNIĘ PLANETY",
            centerX,
            centerY - 20f,
            textPaint
        )

        smallPaint.textAlign =
            Paint.Align.CENTER

        smallPaint.textSize =
            17f

        smallPaint.color =
            Color.rgb(
                190,
                255,
                215
            )

        canvas.drawText(
            "PRĘDKOŚĆ KOŃCOWA: ${abs(velocity).toInt()} km/h",
            centerX,
            centerY + 25f,
            smallPaint
        )

        titlePaint.textSize =
            22f

        titlePaint.color =
            Color.rgb(
                255,
                220,
                80
            )

        canvas.drawText(
            "+$reward MONET",
            centerX,
            centerY + 67f,
            titlePaint
        )

        smallPaint.textSize =
            15f

        smallPaint.color =
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
            "MISJA ZAKOŃCZONA",
            centerX,
            centerY + 108f,
            smallPaint
        )

        smallPaint.textAlign =
            Paint.Align.LEFT
    }

    private fun drawFailure(
        canvas: Canvas
    ) {

        val centerX =
            width / 2f

        val centerY =
            height / 2f

        val pulse =
            (
                sin(
                    animationTime * 7f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        panelPaint.color =
            Color.argb(
                240,
                65,
                8,
                12
            )

        canvas.drawRoundRect(
            15f,
            centerY - 155f,
            width - 15f,
            centerY + 155f,
            25f,
            25f,
            panelPaint
        )

        borderPaint.color =
            Color.rgb(
                255,
                60,
                60
            )

        canvas.drawRoundRect(
            15f,
            centerY - 155f,
            width - 15f,
            centerY + 155f,
            25f,
            25f,
            borderPaint
        )

        titlePaint.textAlign =
            Paint.Align.CENTER

        titlePaint.textSize =
            28f

        titlePaint.color =
            Color.rgb(
                255,
                70,
                70
            )

        canvas.drawText(
            "LĄDOWANIE NIEUDANE",
            centerX,
            centerY - 92f,
            titlePaint
        )

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            19f

        textPaint.color =
            Color.WHITE

        canvas.drawText(
            "RAKIETA NIE UTRZYMAŁA",
            centerX,
            centerY - 48f,
            textPaint
        )

        canvas.drawText(
            "BEZPIECZNEJ PRĘDKOŚCI",
            centerX,
            centerY - 20f,
            textPaint
        )

        smallPaint.textAlign =
            Paint.Align.CENTER

        smallPaint.textSize =
            17f

        smallPaint.color =
            Color.rgb(
                255,
                190,
                190
            )

        canvas.drawText(
            "PRĘDKOŚĆ KOŃCOWA: ${abs(velocity).toInt()} km/h",
            centerX,
            centerY + 25f,
            smallPaint
        )

        titlePaint.textSize =
            21f

        titlePaint.color =
            Color.rgb(
                255,
                150,
                150
            )

        canvas.drawText(
            "NAGRODA: 0 MONET",
            centerX,
            centerY + 67f,
            titlePaint
        )

        smallPaint.textSize =
            15f

        smallPaint.color =
            Color.argb(
                (
                    150f +
                        pulse * 105f
                    ).toInt(),
                255,
                220,
                220
            )

        canvas.drawText(
            "MISJA NIEZALICZONA",
            centerX,
            centerY + 108f,
            smallPaint
        )

        smallPaint.textAlign =
            Paint.Align.LEFT
    }

    override fun onDetachedFromWindow() {

        handler.removeCallbacks(
            updateRunnable
        )

        super.onDetachedFromWindow()
    }
}
