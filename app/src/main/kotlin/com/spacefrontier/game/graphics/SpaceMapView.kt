package com.spacefrontier.game.graphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import com.spacefrontier.game.models.Planet
import com.spacefrontier.game.models.PlanetProgress

class SpaceMapView(
    context: Context
) : View(context) {

    private val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val linePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val planetPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val glowPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val smallTextPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val rocketPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var progress: List<PlanetProgress> =
        emptyList()

    private var currentPlanetIndex =
        0

    private var selectedPlanetIndex =
        0

    private var selectedListener:
            ((Int) -> Unit)? = null

    private val planets =
        listOf(
            Planet(
                name = "Luna",
                emoji = "🌙",
                targetAltitude = 120f,
                reward = 100
            ),
            Planet(
                name = "Mars",
                emoji = "🔴",
                targetAltitude = 180f,
                reward = 250
            ),
            Planet(
                name = "Europa",
                emoji = "🔵",
                targetAltitude = 240f,
                reward = 450
            ),
            Planet(
                name = "Titan",
                emoji = "🟠",
                targetAltitude = 320f,
                reward = 700
            ),
            Planet(
                name = "Neptune",
                emoji = "🔵",
                targetAltitude = 450f,
                reward = 1200
            )
        )

    init {

        backgroundPaint.color =
            Color.rgb(
                3,
                7,
                18
            )

        linePaint.color =
            Color.rgb(
                60,
                100,
                135
            )

        linePaint.strokeWidth =
            5f

        linePaint.style =
            Paint.Style.STROKE

        planetPaint.style =
            Paint.Style.FILL

        glowPaint.style =
            Paint.Style.FILL

        textPaint.color =
            Color.WHITE

        textPaint.textSize =
            18f

        textPaint.typeface =
            Typeface.DEFAULT_BOLD

        smallTextPaint.color =
            Color.LTGRAY

        smallTextPaint.textSize =
            12f

        rocketPaint.color =
            Color.WHITE

        rocketPaint.style =
            Paint.Style.FILL

        isClickable = true
    }

    fun setData(
        progress: List<PlanetProgress>,
        currentPlanetIndex: Int,
        selectedPlanetIndex: Int =
            currentPlanetIndex,
        listener: ((Int) -> Unit)? = null
    ) {

        this.progress =
            progress

        this.currentPlanetIndex =
            currentPlanetIndex

        this.selectedPlanetIndex =
            selectedPlanetIndex

        this.selectedListener =
            listener

        invalidate()
    }

    fun setSelectedPlanet(
        index: Int
    ) {

        if (
            index in planets.indices &&
            isUnlocked(index)
        ) {

            selectedPlanetIndex =
                index

            invalidate()
        }
    }

    private fun isUnlocked(
        index: Int
    ): Boolean {

        if (
            index !in progress.indices
        ) {
            return index == 0
        }

        return progress[index].unlocked
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        canvas.drawColor(
            backgroundPaint.color
        )

        drawStars(
            canvas
        )

        drawRoute(
            canvas
        )

        drawPlanets(
            canvas
        )

        drawRocket(
            canvas
        )

        drawSelectedInfo(
            canvas
        )
    }

    private fun drawStars(
        canvas: Canvas
    ) {

        val width =
            width.toFloat()

        val height =
            height.toFloat()

        val starPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        starPaint.color =
            Color.WHITE

        val stars =
            arrayOf(
                floatArrayOf(.08f, .12f, 2f),
                floatArrayOf(.18f, .25f, 3f),
                floatArrayOf(.31f, .10f, 2f),
                floatArrayOf(.43f, .20f, 2f),
                floatArrayOf(.57f, .09f, 3f),
                floatArrayOf(.69f, .23f, 2f),
                floatArrayOf(.82f, .12f, 2f),
                floatArrayOf(.93f, .28f, 3f),

                floatArrayOf(.12f, .48f, 2f),
                floatArrayOf(.26f, .59f, 3f),
                floatArrayOf(.41f, .50f, 2f),
                floatArrayOf(.55f, .62f, 2f),
                floatArrayOf(.72f, .49f, 3f),
                floatArrayOf(.88f, .58f, 2f),

                floatArrayOf(.06f, .78f, 3f),
                floatArrayOf(.21f, .88f, 2f),
                floatArrayOf(.36f, .75f, 2f),
                floatArrayOf(.51f, .90f, 3f),
                floatArrayOf(.68f, .79f, 2f),
                floatArrayOf(.84f, .91f, 3f),
                floatArrayOf(.95f, .75f, 2f)
            )

        stars.forEach { star ->

            canvas.drawCircle(
                width * star[0],
                height * star[1],
                star[2],
                starPaint
            )
        }
    }

    private fun planetX(
        index: Int
    ): Float {

        if (planets.size <= 1) {
            return width / 2f
        }

        val left =
            width * 0.10f

        val right =
            width * 0.90f

        return left +
                (
                    (right - left) /
                            (planets.size - 1)
                    ) * index
    }

    private fun planetY(): Float =
        height * 0.43f

    private fun drawRoute(
        canvas: Canvas
    ) {

        val path =
            Path()

        planets.indices.forEach { index ->

            val x =
                planetX(index)

            val y =
                planetY()

            if (index == 0) {

                path.moveTo(
                    x,
                    y
                )

            } else {

                val previousX =
                    planetX(index - 1)

                val previousY =
                    planetY()

                path.lineTo(
                    (
                        previousX +
                                x
                        ) / 2f,
                    previousY - 35f
                )

                path.lineTo(
                    x,
                    y
                )
            }
        }

        linePaint.alpha =
            255

        canvas.drawPath(
            path,
            linePaint
        )
    }

    private fun planetColor(
        index: Int
    ): Int {

        return when (index) {

            0 ->
                Color.rgb(
                    180,
                    185,
                    200
                )

            1 ->
                Color.rgb(
                    210,
                    75,
                    45
                )

            2 ->
                Color.rgb(
                    90,
                    170,
                    220
                )

            3 ->
                Color.rgb(
                    210,
                    155,
                    75
                )

            4 ->
                Color.rgb(
                    70,
                    120,
                    210
                )

            else ->
                Color.WHITE
        }
    }

    private fun drawPlanets(
        canvas: Canvas
    ) {

        planets.forEachIndexed {
                index,
                planet ->

            val x =
                planetX(index)

            val y =
                planetY()

            val unlocked =
                isUnlocked(index)

            val selected =
                index ==
                        selectedPlanetIndex

            val current =
                index ==
                        currentPlanetIndex

            if (selected) {

                glowPaint.color =
                    Color.argb(
                        65,
                        85,
                        255,
                        170
                    )

                canvas.drawCircle(
                    x,
                    y,
                    50f,
                    glowPaint
                )
            }

            if (current) {

                glowPaint.color =
                    Color.argb(
                        55,
                        80,
                        190,
                        255
                    )

                canvas.drawCircle(
                    x,
                    y,
                    42f,
                    glowPaint
                )
            }

            planetPaint.color =
                if (unlocked) {
                    planetColor(index)
                } else {
                    Color.rgb(
                        55,
                        60,
                        70
                    )
                }

            canvas.drawCircle(
                x,
                y,
                30f,
                planetPaint
            )

            if (!unlocked) {

                val lockPaint =
                    Paint(Paint.ANTI_ALIAS_FLAG)

                lockPaint.color =
                    Color.WHITE

                lockPaint.textSize =
                    22f

                lockPaint.textAlign =
                    Paint.Align.CENTER

                lockPaint.typeface =
                    Typeface.DEFAULT_BOLD

                canvas.drawText(
                    "🔒",
                    x,
                    y + 8f,
                    lockPaint
                )
            }

            if (selected) {

                val ringPaint =
                    Paint(Paint.ANTI_ALIAS_FLAG)

                ringPaint.color =
                    Color.rgb(
                        85,
                        255,
                        170
                    )

                ringPaint.style =
                    Paint.Style.STROKE

                ringPaint.strokeWidth =
                    4f

                canvas.drawCircle(
                    x,
                    y,
                    38f,
                    ringPaint
                )
            }

            val namePaint =
                Paint(Paint.ANTI_ALIAS_FLAG)

            namePaint.color =
                if (unlocked) {
                    Color.WHITE
                } else {
                    Color.GRAY
                }

            namePaint.textSize =
                13f

            namePaint.typeface =
                Typeface.DEFAULT_BOLD

            namePaint.textAlign =
                Paint.Align.CENTER

            canvas.drawText(
                planet.name,
                x,
                y + 56f,
                namePaint
            )

            val missionCount =
                if (
                    index <
                    progress.size
                ) {
                    progress[index]
                        .missionsCompleted
                } else {
                    0
                }

            val infoPaint =
                Paint(Paint.ANTI_ALIAS_FLAG)

            infoPaint.color =
                if (unlocked) {
                    Color.LTGRAY
                } else {
                    Color.DKGRAY
                }

            infoPaint.textSize =
                9f

            infoPaint.textAlign =
                Paint.Align.CENTER

            canvas.drawText(
                if (unlocked) {
                    "MISJE: $missionCount"
                } else {
                    "ZABLOKOWANA"
                },
                x,
                y + 71f,
                infoPaint
            )
        }
    }

    private fun drawRocket(
        canvas: Canvas
    ) {

        val x =
            if (
                selectedPlanetIndex in
                planets.indices
            ) {
                planetX(
                    selectedPlanetIndex
                )
            } else {
                planetX(0)
            }

        val y =
            planetY() - 66f

        val body =
            Path()

        body.moveTo(
            x,
            y - 16f
        )

        body.lineTo(
            x + 8f,
            y + 8f
        )

        body.lineTo(
            x - 8f,
            y + 8f
        )

        body.close()

        canvas.drawPath(
            body,
            rocketPaint
        )

        val windowPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        windowPaint.color =
            Color.rgb(
                60,
                180,
                255
            )

        canvas.drawCircle(
            x,
            y - 2f,
            3.5f,
            windowPaint
        )

        val flamePaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        flamePaint.color =
            Color.rgb(
                255,
                150,
                40
            )

        canvas.drawCircle(
            x,
            y + 13f,
            5f,
            flamePaint
        )
    }

    private fun drawSelectedInfo(
        canvas: Canvas
    ) {

        if (
            selectedPlanetIndex !in
            planets.indices
        ) {
            return
        }

        val planet =
            planets[
                selectedPlanetIndex
            ]

        val unlocked =
            isUnlocked(
                selectedPlanetIndex
            )

        val panelPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        panelPaint.color =
            Color.argb(
                225,
                10,
                20,
                35
            )

        canvas.drawRoundRect(
            22f,
            height * 0.72f,
            width - 22f,
            height * 0.90f,
            22f,
            22f,
            panelPaint
        )

        val title =
            if (unlocked) {
                "${planet.emoji} ${planet.name}"
            } else {
                "🔒 ${planet.name}"
            }

        textPaint.textAlign =
            Paint.Align.CENTER

        canvas.drawText(
            title,
            width / 2f,
            height * 0.77f,
            textPaint
        )

        smallTextPaint.textAlign =
            Paint.Align.CENTER

        canvas.drawText(
            "Cel misji: ${planet.targetAltitude.toInt()} km",
            width / 2f,
            height * 0.815f,
            smallTextPaint
        )

        canvas.drawText(
            "Nagroda: 💰 ${planet.reward}",
            width / 2f,
            height * 0.85f,
            smallTextPaint
        )

        canvas.drawText(
            if (unlocked) {
                "DOTKNIJ PLANETY, ABY JĄ WYBRAĆ"
            } else {
                "UKOŃCZ POPRZEDNIĄ PLANETĘ"
            },
            width / 2f,
            height * 0.885f,
            smallTextPaint
        )
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (
            event.action !=
            MotionEvent.ACTION_UP
        ) {
            return true
        }

        val touchX =
            event.x

        val touchY =
            event.y

        if (
            touchY <
            planetY() - 65f ||
            touchY >
            planetY() + 65f
        ) {
            return true
        }

        planets.indices.forEach { index ->

            val distance =
                kotlin.math.sqrt(
                    (
                        touchX -
                                planetX(index)
                        ) *
                        (
                            touchX -
                                    planetX(index)
                            ) +
                            (
                                touchY -
                                        planetY()
                                ) *
                                (
                                    touchY -
                                            planetY()
                                    )
                )

            if (
                distance <= 48f &&
                isUnlocked(index)
            ) {

                selectedPlanetIndex =
                    index

                selectedListener?.invoke(
                    index
                )

                invalidate()

                return true
            }
        }

        return true
    }
}
