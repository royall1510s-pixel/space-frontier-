package com.spacefrontier.game.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import com.spacefrontier.game.logic.FuelStatus
import com.spacefrontier.game.models.GameState
import com.spacefrontier.game.models.Rocket
import kotlin.math.roundToInt

class FlightHudView(
    context: Context
) : View(context) {

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
            24f

        titlePaint.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

        valuePaint.color =
            Color.WHITE

        valuePaint.textSize =
            22f

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
            17f

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
            25f

        warningPaint.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

        setLayerType(
            View.LAYER_TYPE_SOFTWARE,
            null
        )
    }

    fun update(
        newRocket: Rocket,
        newPhase: GameState.GamePhase
    ) {

        rocket =
            newRocket

        gamePhase =
            newPhase

        fuelStatus =
            FuelStatus.from(
                fuel = newRocket.fuel,
                maxFuel = newRocket.maxFuel
            )

        invalidate()
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)

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
            16f,
            16f,
            panelWidth - 16f,
            panelHeight - 16f,
            20f,
            20f,
            backgroundPaint
        )

        backgroundPaint.clearShadowLayer()

        var y =
            48f

        canvas.drawText(
            "🚀 ${currentRocket.name}",
            30f,
            y,
            titlePaint
        )

        y += 38f

        canvas.drawText(
            "FAZA LOTU",
            30f,
            y,
            smallPaint
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
                    "LĄDOWANIE UDANE"

                GameState.GamePhase.LANDED_FAILED ->
                    "KATASTROFA"

                else ->
                    "GOTOWOŚĆ"
            }

        canvas.drawText(
            stageText,
            panelWidth - 150f,
            y,
            valuePaint
        )

        y += 45f

        canvas.drawText(
            "🌍 WYSOKOŚĆ",
            30f,
            y,
            smallPaint
        )

        canvas.drawText(
            "${currentRocket.altitude.roundToInt()} km",
            30f,
            y + 29f,
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
            y + 29f,
            valuePaint
        )

        y += 75f

        canvas.drawText(
            "⛽ PALIWO",
            30f,
            y,
            smallPaint
        )

        canvas.drawText(
            fuelStatus.percentageText,
            panelWidth - 85f,
            y,
            valuePaint
        )

        val barLeft =
            30f

        val barRight =
            panelWidth - 30f

        val barTop =
            y + 14f

        val barBottom =
            barTop + 18f

        canvas.drawRoundRect(
            barLeft,
            barTop,
            barRight,
            barBottom,
            9f,
            9f,
            fuelBackgroundPaint
        )

        fuelPaint.color =
            when {

                fuelStatus.isCritical ->
                    Color.rgb(
                        255,
                        60,
                        50
                    )

                fuelStatus.isLow ->
                    Color.rgb(
                        255,
                        190,
                        50
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
            9f,
            9f,
            fuelPaint
        )

        y += 60f

        canvas.drawText(
            "⛽ ${fuelStatus.displayText}",
            30f,
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
                30f,
                panelHeight - 25f,
                warningPaint
            )
        }
    }
}
