package com.spacefrontier.game.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import com.spacefrontier.game.models.PlanetMission
import com.spacefrontier.game.models.Planets
import com.spacefrontier.game.models.Rocket

class MissionPanelView(
    context: Context
) : View(context) {

    private val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val titlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val smallPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val accentPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val successPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val warningPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var mission: PlanetMission? = null

    private var rocket: Rocket? = null

    init {

        backgroundPaint.color =
            Color.rgb(
                12,
                25,
                42
            )

        titlePaint.color =
            Color.WHITE

        titlePaint.textSize =
            42f

        titlePaint.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

        textPaint.color =
            Color.rgb(
                220,
                230,
                240
            )

        textPaint.textSize =
            30f

        smallPaint.color =
            Color.rgb(
                160,
                180,
                200
            )

        smallPaint.textSize =
            24f

        accentPaint.color =
            Color.rgb(
                85,
                255,
                170
            )

        accentPaint.textSize =
            28f

        accentPaint.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

        successPaint.color =
            Color.rgb(
                85,
                255,
                170
            )

        successPaint.textSize =
            26f

        successPaint.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

        warningPaint.color =
            Color.rgb(
                255,
                100,
                80
            )

        warningPaint.textSize =
            26f

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

    fun setMission(
        newMission: PlanetMission,
        newRocket: Rocket? = null
    ) {

        mission =
            newMission

        rocket =
            newRocket

        invalidate()
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        val viewWidth =
            width.toFloat()

        val viewHeight =
            height.toFloat()

        canvas.drawColor(
            Color.rgb(
                5,
                9,
                20
            )
        )

        backgroundPaint.color =
            Color.rgb(
                12,
                25,
                42
            )

        backgroundPaint.setShadowLayer(
            18f,
            0f,
            6f,
            Color.BLACK
        )

        canvas.drawRoundRect(
            20f,
            20f,
            viewWidth - 20f,
            viewHeight - 20f,
            28f,
            28f,
            backgroundPaint
        )

        backgroundPaint.clearShadowLayer()

        val currentMission =
            mission
                ?: return

        var y =
            70f

        canvas.drawText(
            "🪐 ${currentMission.planetName}",
            45f,
            y,
            titlePaint
        )

        y += 48f

        canvas.drawText(
            "MISJA EKSPEDYCYJNA",
            45f,
            y,
            smallPaint
        )

        y += 55f

        canvas.drawText(
            "📏 Dystans: ${currentMission.distance} km",
            45f,
            y,
            textPaint
        )

        y += 43f

        canvas.drawText(
            "⚠ Trudność: ${currentMission.difficulty}/5",
            45f,
            y,
            textPaint
        )

        y += 43f

        canvas.drawText(
            "⛽ Zużycie paliwa: x${formatMultiplier(currentMission.fuelMultiplier)}",
            45f,
            y,
            textPaint
        )

        y += 43f

        canvas.drawText(
            "💰 Nagroda: x${formatMultiplier(currentMission.rewardMultiplier)}",
            45f,
            y,
            accentPaint
        )

        y += 58f

        drawDifficultyBar(
            canvas = canvas,
            x = 45f,
            y = y,
            difficulty = currentMission.difficulty
        )

        y += 65f

        drawRocketRequirement(
            canvas = canvas,
            x = 45f,
            y = y
        )

        y += 110f

        drawDescription(
            canvas = canvas,
            text = currentMission.description,
            x = 45f,
            startY = y,
            maxWidth = viewWidth - 90f
        )
    }

    private fun drawDifficultyBar(
        canvas: Canvas,
        x: Float,
        y: Float,
        difficulty: Int
    ) {

        canvas.drawText(
            "POZIOM ZAGROŻENIA",
            x,
            y,
            smallPaint
        )

        val barTop =
            y + 15f

        val barHeight =
            18f

        val barWidth =
            width - 90f

        backgroundPaint.color =
            Color.rgb(
                35,
                48,
                65
            )

        canvas.drawRoundRect(
            x,
            barTop,
            x + barWidth,
            barTop + barHeight,
            10f,
            10f,
            backgroundPaint
        )

        backgroundPaint.color =
            Color.rgb(
                255,
                107,
                0
            )

        val progress =
            difficulty
                .coerceIn(
                    1,
                    5
                ) / 5f

        canvas.drawRoundRect(
            x,
            barTop,
            x + barWidth * progress,
            barTop + barHeight,
            10f,
            10f,
            backgroundPaint
        )

        backgroundPaint.color =
            Color.rgb(
                12,
                25,
                42
            )
    }

    private fun drawRocketRequirement(
        canvas: Canvas,
        x: Float,
        y: Float
    ) {

        val currentRocket =
            rocket

        if (currentRocket == null) {

            canvas.drawText(
                "🚀 Wybierz rakietę",
                x,
                y,
                smallPaint
            )

            return
        }

        val currentMission =
            mission
                ?: return

        val requiredAltitude =
            Planets.all
                .firstOrNull {
                    it.name == currentMission.planetName
                }
                ?.targetAltitude
                ?: currentMission.distance.toFloat()

        val rocketAltitude =
            currentRocket.maxAltitude

        val canStart =
            rocketAltitude >= requiredAltitude

        canvas.drawText(
            "🚀 WYMAGANIA RAKIETY",
            x,
            y,
            smallPaint
        )

        canvas.drawText(
            "Rakieta: ${currentRocket.name}",
            x,
            y + 35f,
            textPaint
        )

        val requirementText =
            "Zasięg: ${rocketAltitude.toInt()} km / wymagane ${requiredAltitude.toInt()} km"

        canvas.drawText(
            requirementText,
            x,
            y + 70f,
            if (canStart) {
                successPaint
            } else {
                warningPaint
            }
        )

        val statusText =
            if (canStart) {
                "✅ RAKIETA GOTOWA DO MISJI"
            } else {
                "🛑 RAKIETA ZA SŁABA"
            }

        canvas.drawText(
            statusText,
            x,
            y + 105f,
            if (canStart) {
                successPaint
            } else {
                warningPaint
            }
        )
    }

    private fun drawDescription(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float
    ) {

        val words =
            text.split(" ")

        var line =
            ""

        var y =
            startY

        for (word in words) {

            val candidate =
                if (line.isEmpty()) {
                    word
                } else {
                    "$line $word"
                }

            if (
                textPaint.measureText(
                    candidate
                ) > maxWidth
            ) {

                if (line.isNotEmpty()) {

                    canvas.drawText(
                        line,
                        x,
                        y,
                        smallPaint
                    )
                }

                line =
                    word

                y += 32f

            } else {

                line =
                    candidate
            }
        }

        if (line.isNotEmpty()) {

            canvas.drawText(
                line,
                x,
                y,
                smallPaint
            )
        }
    }

    private fun formatMultiplier(
        value: Float
    ): String {

        return if (
            value % 1f == 0f
        ) {

            value.toInt()
                .toString()

        } else {

            String.format(
                java.util.Locale.US,
                "%.2f",
                value
            )
        }
    }
}
