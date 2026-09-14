package com.spacefrontier.game.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import com.spacefrontier.game.models.PlanetMission
import com.spacefrontier.game.models.Rocket

class MissionPanelView(
    context: Context
) : View(context) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val successPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val warningPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mission: PlanetMission? = null
    private var rocket: Rocket? = null

    init {
        backgroundPaint.color = Color.rgb(12, 25, 42)

        titlePaint.color = Color.WHITE
        titlePaint.textSize = 42f
        titlePaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        textPaint.color = Color.rgb(220, 230, 240)
        textPaint.textSize = 30f

        smallPaint.color = Color.rgb(160, 180, 200)
        smallPaint.textSize = 24f

        accentPaint.color = Color.rgb(85, 255, 170)
        accentPaint.textSize = 28f
        accentPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        successPaint.color = Color.rgb(85, 255, 170)
        successPaint.textSize = 28f
        successPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        warningPaint.color = Color.rgb(255, 100, 80)
        warningPaint.textSize = 28f
        warningPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    fun setMission(
        newMission: PlanetMission,
        newRocket: Rocket? = null
    ) {
        mission = newMission
        rocket = newRocket
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        canvas.drawColor(Color.rgb(5, 9, 20))

        backgroundPaint.color = Color.rgb(12, 25, 42)

        backgroundPaint.setShadowLayer(
            18f,
            0f,
            6f,
            Color.BLACK
        )

        canvas.drawRoundRect(
            20f,
            20f,
            width - 20f,
            height - 20f,
            28f,
            28f,
            backgroundPaint
        )

        backgroundPaint.clearShadowLayer()

        val currentMission = mission
            ?: return

        var y = 70f

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
            y = y,
            maxWidth = width - 90f
        )

        y += 105f

        drawDescription(
            canvas = canvas,
            text = currentMission.description,
            x = 45f,
            startY = y,
            maxWidth = width - 90f
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

        val barTop = y + 15f
        val barHeight = 18f
        val barWidth = width - 90f

        backgroundPaint.color =
            Color.rgb(35, 48, 65)

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
            Color.rgb(255, 107, 0)

        val progress =
            difficulty.coerceIn(1,
