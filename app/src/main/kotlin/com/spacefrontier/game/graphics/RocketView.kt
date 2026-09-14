package com.spacefrontier.game.graphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.spacefrontier.game.models.GameState
import com.spacefrontier.game.models.Planet
import com.spacefrontier.game.models.Rocket
import kotlin.math.cos
import kotlin.math.sin

class RocketView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr
) {

    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var currentRocket: Rocket? = null

    private var currentPlanet: Planet? = null

    private var currentPhase =
        GameState.GamePhase.AWAITING_FIRST_TAP

    private var animationTime =
        0f

    fun updateRocket(
        rocket: Rocket
    ) {

        currentRocket =
            rocket

        invalidate()
    }

    fun updateScene(
        rocket: Rocket,
        planet: Planet?,
        phase: GameState.GamePhase
    ) {

        currentRocket =
            rocket

        currentPlanet =
            planet

        currentPhase =
            phase

        invalidate()
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        val rocket =
            currentRocket
                ?: return

        animationTime +=
            0.08f

        drawSpaceBackground(
            canvas
        )

        drawStars(
            canvas,
            rocket
        )

        drawAsteroids(
            canvas,
            rocket
        )

        if (
            currentPhase ==
            GameState.GamePhase.LANDING_SEQUENCE ||
            currentPhase ==
            GameState.GamePhase.LANDED_SUCCESS ||
            currentPhase ==
            GameState.GamePhase.LANDED_FAILED
        ) {

            drawLandingPlanet(
                canvas,
                rocket
            )
        }

        drawRocket(
            canvas,
            rocket
        )

        drawHud(
            canvas,
            rocket
        )

        postInvalidateOnAnimation()
    }

    private fun drawSpaceBackground(
        canvas: Canvas
    ) {

        val topColor =
            when (
                currentPlanet?.name
            ) {

                "Luna" ->
                    Color.rgb(
                        4,
                        8,
                        20
                    )

                "Mars" ->
                    Color.rgb(
                        20,
                        6,
                        8
                    )

                "Europa" ->
                    Color.rgb(
                        4,
                        15,
                        25
                    )

                "Titan" ->
                    Color.rgb(
                        24,
                        11,
                        5
                    )

                "Neptune" ->
                    Color.rgb(
                        3,
                        8,
                        30
                    )

                else ->
                    Color.rgb(
                        1,
                        5,
                        20
                    )
            }

        val bottomColor =
            when (
                currentPlanet?.name
            ) {

                "Luna" ->
                    Color.rgb(
                        10,
                        18,
                        34
                    )

                "Mars" ->
                    Color.rgb(
                        34,
                        12,
                        14
                    )

                "Europa" ->
                    Color.rgb(
                        8,
                        30,
                        48
                    )

                "Titan" ->
                    Color.rgb(
                        45,
                        23,
                        10
                    )

                "Neptune" ->
                    Color.rgb(
                        5,
                        18,
                        55
                    )

                else ->
                    Color.rgb(
                        5,
                        18,
                        45
                    )
            }

        paint.shader =
            LinearGradient(
                0f,
                0f,
                0f,
                height.toFloat(),
                topColor,
                bottomColor,
                Shader.TileMode.CLAMP
            )

        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            paint
        )

        paint.shader =
            null

        paint.color =
            Color.argb(
                35,
                70,
                120,
                220
            )

        canvas.drawCircle(
            width * 0.18f,
            height * 0.25f,
            width * 0.27f,
            paint
        )

        paint.color =
            Color.argb(
                28,
                150,
                70,
                220
            )

        canvas.drawCircle(
            width * 0.84f,
            height * 0.68f,
            width * 0.30f,
            paint
        )
    }

    private fun drawStars(
        canvas: Canvas,
        rocket: Rocket
    ) {

        val speed =
            rocket.velocity.coerceAtLeast(
                0f
            )

        val movement =
            (
                animationTime *
                        (
                            8f +
                                    speed * 0.12f
                            )
                ) %
                    height.coerceAtLeast(
                        1
                    )

        for (i in 0..110) {

            val x =
                (
                    (
                        i * 97 +
                                31
                        ) %
                            width.coerceAtLeast(
                                1
                            )
                    ).toFloat()

            var y =
                (
                    (
                        i * 173 +
                                47
                        ) %
                            height.coerceAtLeast(
                                1
                            )
                    ).toFloat() +
                        movement

            if (
                y >
                height
            ) {

                y -=
                    height
            }

            val size =
                when (
                    i % 5
                ) {

                    0 -> 2.4f

                    1 -> 1.5f

                    2 -> 1.0f

                    3 -> 2.0f

                    else -> 0.8f
                }

            paint.color =
                when (
                    i % 9
                ) {

                    0 ->
                        Color.rgb(
                            130,
                            210,
                            255
                        )

                    1 ->
                        Color.rgb(
                            190,
                            150,
                            255
                        )

                    else ->
                        Color.WHITE
                }

            canvas.drawCircle(
                x,
                y,
                size,
                paint
            )
        }

        drawBrightStar(
            canvas,
            width * 0.15f,
            height * 0.18f
        )

        drawBrightStar(
            canvas,
            width * 0.78f,
            height * 0.30f
        )

        drawBrightStar(
            canvas,
            width * 0.62f,
            height * 0.78f
        )
    }

    private fun drawBrightStar(
        canvas: Canvas,
        x: Float,
        y: Float
    ) {

        paint.color =
            Color.argb(
                220,
                225,
                245,
                255
            )

        paint.style =
            Paint.Style.FILL

        canvas.drawCircle(
            x,
            y,
            3f,
            paint
        )

        paint.strokeWidth =
            1.5f

        canvas.drawLine(
            x - 12f,
            y,
            x + 12f,
            y,
            paint
        )

        canvas.drawLine(
            x,
            y - 12f,
            x,
            y + 12f,
            paint
        )
    }

    private fun drawAsteroids(
        canvas: Canvas,
        rocket: Rocket
    ) {

        val movement =
            (
                animationTime *
                        (
                            5f +
                                    rocket.velocity * 0.05f
                            )
                ) %
                    (
                        height +
                                250f
                        )

        val asteroidData =
            listOf(
                Triple(
                    0.14f,
                    0.20f,
                    34f
                ),
                Triple(
                    0.83f,
                    0.34f,
                    25f
                ),
                Triple(
                    0.10f,
                    0.68f,
                    20f
                ),
                Triple(
                    0.88f,
                    0.78f,
                    42f
                ),
                Triple(
                    0.28f,
                    0.48f,
                    15f
                ),
                Triple(
                    0.70f,
                    0.13f,
                    18f
                )
            )

        asteroidData.forEachIndexed {
                index,
                data ->

            val x =
                width * data.first

            var y =
                height * data.second +
                        movement *
                        if (
                            index % 2 == 0
                        ) {
                            1f
                        } else {
                            -1f
                        }

            if (
                y >
                height + 80f
            ) {

                y -=
                    height + 160f
            }

            if (
                y <
                -80f
            ) {

                y +=
                    height + 160f
            }

            drawAsteroid(
                canvas,
                x,
                y,
                data.third,
                index
            )
        }
    }

    private fun drawAsteroid(
        canvas: Canvas,
        x: Float,
        y: Float,
        radius: Float,
        index: Int
    ) {

        val path =
            Path()

        val points =
            9

        for (i in 0 until points) {

            val angle =
                Math.PI * 2.0 *
                        i /
                        points

            val variation =
                0.78f +
                        (
                            (
                                i * 17 +
                                        index * 11
                                ) %
                                    30
                            ) /
                            100f

            val px =
                x +
                        (
                            cos(angle) *
                                    radius *
                                    variation
                            ).toFloat()

            val py =
                y +
                        (
                            sin(angle) *
                                    radius *
                                    variation
                            ).toFloat()

            if (
                i == 0
            ) {

                path.moveTo(
                    px,
                    py
                )

            } else {

                path.lineTo(
                    px,
                    py
                )
            }
        }

        path.close()

        paint.color =
            Color.rgb(
                65,
                68,
                78
            )

        paint.style =
            Paint.Style.FILL

        canvas.drawPath(
            path,
            paint
        )

        paint.color =
            Color.rgb(
                105,
                108,
                120
            )

        canvas.drawCircle(
            x - radius * 0.25f,
            y - radius * 0.25f,
            radius * 0.28f,
            paint
        )

        paint.color =
            Color.rgb(
                42,
                44,
                52
            )

        canvas.drawCircle(
            x + radius * 0.25f,
            y + radius * 0.18f,
            radius * 0.18f,
            paint
        )

        canvas.drawCircle(
            x - radius * 0.18f,
            y + radius * 0.30f,
            radius * 0.12f,
            paint
        )
    }

    private fun drawLandingPlanet(
        canvas: Canvas,
        rocket: Rocket
    ) {

        val planet =
            currentPlanet
                ?: return

        val centerX =
            width / 2f

        val baseY =
            height * 0.94f

        val radius =
            width * 0.43f

        val planetColor =
            when (
                planet.name
            ) {

                "Luna" ->
                    Color.rgb(
                        145,
                        145,
                        155
                    )

                "Mars" ->
                    Color.rgb(
                        185,
                        62,
                        40
                    )

                "Europa" ->
                    Color.rgb(
                        165,
                        205,
                        230
                    )

                "Titan" ->
                    Color.rgb(
                        195,
                        135,
                        55
                    )

                "Neptune" ->
                    Color.rgb(
                        48,
                        95,
                        215
                    )

                else ->
                    Color.rgb(
                        70,
                        120,
                        180
                    )
            }

        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.argb(
                65,
                70,
                170,
                255
            )

        canvas.drawCircle(
            centerX,
            baseY,
            radius * 1.12f,
            paint
        )

        paint.shader =
            RadialGradient(
                centerX -
                        radius * 0.34f,
                baseY -
                        radius * 0.36f,
                radius,
                Color.WHITE,
                planetColor,
                Shader.TileMode.CLAMP
            )

        canvas.drawCircle(
            centerX,
            baseY,
            radius,
            paint
        )

        paint.shader =
            null

        drawPlanetSurface(
            canvas,
            centerX,
            baseY,
            radius,
            planet.name
        )

        val pulse =
            (
                sin(
                    animationTime * 4f
                ) + 1f
                ) / 2f

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            4f +
                    pulse * 3f

        paint.color =
            when (
                currentPhase
            ) {

                GameState.GamePhase.LANDED_SUCCESS ->
                    Color.rgb(
                        60,
                        255,
                        120
                    )

                GameState.GamePhase.LANDED_FAILED ->
                    Color.rgb(
                        255,
                        70,
                        70
                    )

                else ->
                    Color.rgb(
                        80,
                        255,
                        150
                    )
            }

        canvas.drawOval(
            centerX -
                    radius * 0.48f,
            baseY -
                    radius * 0.10f,
            centerX +
                    radius * 0.48f,
            baseY +
                    radius * 0.10f,
            paint
        )

        paint.style =
            Paint.Style.FILL

        if (
            currentPhase ==
            GameState.GamePhase.LANDING_SEQUENCE
        ) {

            paint.color =
                Color.argb(
                    70,
                    80,
                    255,
                    150
                )

            canvas.drawCircle(
                centerX,
                baseY -
                        radius * 0.12f,
                radius *
                        (
                            0.15f +
                                    (
                                        rocket.altitude /
                                                300f
                                        ).coerceIn(
                                            0f,
                                            1f
                                        ) *
                                    0.18f
                            ),
                paint
            )
        }

        paint.color =
            Color.WHITE

        paint.textAlign =
            Paint.Align.CENTER

        paint.textSize =
            24f

        canvas.drawText(
            planet.name.uppercase(),
            centerX,
            height - 22f,
            paint
        )
    }

    private fun drawPlanetSurface(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        planetName: String
    ) {

        when (
            planetName
        ) {

            "Luna" -> {

                drawCrater(
                    canvas,
                    centerX -
                            radius * 0.28f,
                    centerY -
                            radius * 0.16f,
                    radius * 0.10f
                )

                drawCrater(
                    canvas,
                    centerX +
                            radius * 0.18f,
                    centerY -
                            radius * 0.28f,
                    radius * 0.07f
                )

                drawCrater(
                    canvas,
                    centerX +
                            radius * 0.28f,
                    centerY +
                            radius * 0.20f,
                    radius * 0.13f
                )
            }

            "Mars" -> {

                drawSurfaceSpot(
                    canvas,
                    centerX -
                            radius * 0.25f,
                    centerY +
                            radius * 0.15f,
                    radius * 0.18f
                )

                drawSurfaceSpot(
                    canvas,
                    centerX +
                            radius * 0.22f,
                    centerY -
                            radius * 0.12f,
                    radius * 0.12f
                )

                drawSurfaceSpot(
                    canvas,
                    centerX +
                            radius * 0.02f,
                    centerY +
                            radius * 0.28f,
                    radius * 0.08f
                )
            }

            "Europa" -> {

                drawIceLine(
                    canvas,
                    centerX -
                            radius * 0.40f,
                    centerY -
                            radius * 0.12f,
                    centerX +
                            radius * 0.25f,
                    centerY +
                            radius * 0.04f
                )

                drawIceLine(
                    canvas,
                    centerX -
                            radius * 0.12f,
                    centerY +
                            radius * 0.30f,
                    centerX +
                            radius * 0.35f,
                    centerY -
                            radius * 0.24f
                )
            }

            "Titan" -> {

                drawSurfaceSpot(
                    canvas,
                    centerX -
                            radius * 0.20f,
                    centerY -
                            radius * 0.18f,
                    radius * 0.14f
                )

                drawSurfaceSpot(
                    canvas,
                    centerX +
                            radius * 0.25f,
                    centerY +
                            radius * 0.18f,
                    radius * 0.16f
                )
            }

            "Neptune" -> {

                drawBelt(
                    canvas,
                    centerX,
                    centerY -
                            radius * 0.08f,
                    radius
                )

                drawBelt(
                    canvas,
                    centerX,
                    centerY +
                            radius * 0.18f,
                    radius
                )
            }
        }
    }

    private fun drawCrater(
        canvas: Canvas,
        x: Float,
        y: Float,
        radius: Float
    ) {

        paint.color =
            Color.argb(
                65,
                50,
                50,
                55
            )

        canvas.drawCircle(
            x,
            y,
            radius,
            paint
        )

        paint.color =
            Color.argb(
                35,
                245,
                245,
                250
            )

        canvas.drawCircle(
            x -
                    radius * 0.20f,
            y -
                    radius * 0.15f,
            radius * 0.45f,
            paint
        )
    }

    private fun drawSurfaceSpot(
        canvas: Canvas,
        x: Float,
        y: Float,
        radius: Float
    ) {

        paint.color =
            Color.argb(
                45,
                0,
                0,
                0
            )

        canvas.drawCircle(
            x,
            y,
            radius,
            paint
        )
    }

    private fun drawIceLine(
        canvas: Canvas,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float
    ) {

        paint.color =
            Color.argb(
                95,
                255,
                255,
                255
            )

        paint.strokeWidth =
            5f

        paint.style =
            Paint.Style.STROKE

        canvas.drawLine(
            startX,
            startY,
            endX,
            endY,
            paint
        )

        paint.style =
            Paint.Style.FILL
    }

    private fun drawBelt(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float
    ) {

        paint.color =
            Color.argb(
                45,
                150,
                200,
                255
            )

        paint.strokeWidth =
            7f

        paint.style =
            Paint.Style.STROKE

        canvas.drawOval(
            centerX -
                    radius * 0.70f,
            centerY -
                    radius * 0.10f,
            centerX +
                    radius * 0.70f,
            centerY +
                    radius * 0.10f,
            paint
        )

        paint.style =
            Paint.Style.FILL
    }

    private fun drawRocket(
        canvas: Canvas,
        rocket: Rocket
    ) {

        val centerX =
            width / 2f

        val altitudeProgress =
            (
                rocket.altitude /
                        300f
                ).coerceIn(
                    0f,
                    1f
                )

        val normalY =
            height * 0.66f -
                    altitudeProgress *
                    height * 0.38f

        val landingOffset =
            if (
                currentPhase ==
                GameState.GamePhase.LANDING_SEQUENCE
            ) {

                (
                    rocket.altitude /
                            150f
                    ).coerceIn(
                        0f,
                        1f
                    ) *
                        height *
                        0.18f

            } else {
                0f
            }

        val centerY =
            normalY +
                    landingOffset

        val rocketWidth =
            (
                width * 0.12f
                ).coerceIn(
                    72f,
                    120f
                )

        val rocketHeight =
            rocketWidth *
                    2.25f

        val sway =
            if (
                currentPhase ==
                GameState.GamePhase.LANDING_SEQUENCE
            ) {

                sin(
                    animationTime * 3f
                ) *
                    1.2f

            } else {

                sin(
                    animationTime * 2.2f
                ) *
                    if (
                        rocket.velocity > 0f
                    ) {
                        2.5f
                    } else {
                        0.5f
                    }
            }

        canvas.save()

        canvas.rotate(
            sway.toFloat(),
            centerX,
            centerY
        )

        drawEngineFlame(
            canvas,
            centerX,
            centerY +
                    rocketHeight * 0.58f,
            rocket
        )

        val bodyPath =
            Path()

        val noseY =
            centerY -
                    rocketHeight * 0.50f

        val bodyTopY =
            centerY -
                    rocketHeight * 0.24f

        val bodyBottomY =
            centerY +
                    rocketHeight * 0.40f

        bodyPath.moveTo(
            centerX,
            noseY
        )

        bodyPath.lineTo(
            centerX +
                    rocketWidth * 0.36f,
            bodyTopY
        )

        bodyPath.lineTo(
            centerX +
                    rocketWidth * 0.50f,
            bodyBottomY
        )

        bodyPath.lineTo(
            centerX -
                    rocketWidth * 0.50f,
            bodyBottomY
        )

        bodyPath.lineTo(
            centerX -
                    rocketWidth * 0.36f,
            bodyTopY
        )

        bodyPath.close()

        paint.shader =
            LinearGradient(
                centerX -
                        rocketWidth * 0.5f,
                0f,
                centerX +
                        rocketWidth * 0.5f,
                0f,
                Color.rgb(
                    220,
                    225,
                    235
                ),
                Color.rgb(
                    95,
                    105,
                    125
                ),
                Shader.TileMode.CLAMP
            )

        canvas.drawPath(
            bodyPath,
            paint
        )

        paint.shader =
            null

        paint.color =
            Color.rgb(
                45,
                55,
                70
            )

        val windowRadius =
            rocketWidth * 0.16f

        canvas.drawCircle(
            centerX,
            centerY -
                    rocketHeight * 0.10f,
            windowRadius,
            paint
        )

        paint.color =
            Color.rgb(
                80,
                205,
                255
            )

        canvas.drawCircle(
            centerX -
                    windowRadius * 0.25f,
            centerY -
                    rocketHeight * 0.13f,
            windowRadius * 0.62f,
            paint
        )

        paint.color =
            Color.rgb(
                180,
                235,
                255
            )

        canvas.drawCircle(
            centerX -
                    windowRadius * 0.42f,
            centerY -
                    rocketHeight * 0.17f,
            windowRadius * 0.18f,
            paint
        )

        paint.color =
            Color.rgb(
                200,
                55,
                50
            )

        val leftFin =
            Path()

        leftFin.moveTo(
            centerX -
                    rocketWidth * 0.32f,
            centerY +
                    rocketHeight * 0.20f
        )

        leftFin.lineTo(
            centerX -
                    rocketWidth * 0.64f,
            centerY +
                    rocketHeight * 0.48f
        )

        leftFin.lineTo(
            centerX -
                    rocketWidth * 0.40f,
            centerY +
                    rocketHeight * 0.45f
        )

        leftFin.close()

        canvas.drawPath(
            leftFin,
            paint
        )

        val rightFin =
            Path()

        rightFin.moveTo(
            centerX +
                    rocketWidth * 0.32f,
            centerY +
                    rocketHeight * 0.20f
        )

        rightFin.lineTo(
            centerX +
                    rocketWidth * 0.64f,
            centerY +
                    rocketHeight * 0.48f
        )

        rightFin.lineTo(
            centerX +
                    rocketWidth * 0.40f,
            centerY +
                    rocketHeight * 0.45f
        )

        rightFin.close()

        canvas.drawPath(
            rightFin,
            paint
        )

        paint.color =
            Color.rgb(
                245,
                245,
                248
            )

        canvas.drawRoundRect(
            centerX -
                    rocketWidth * 0.20f,
            centerY +
                    rocketHeight * 0.18f,
            centerX +
                    rocketWidth * 0.20f,
            centerY +
                    rocketHeight * 0.39f,
            8f,
            8f,
            paint
        )

        canvas.restore()
    }

    private fun drawEngineFlame(
        canvas: Canvas,
        x: Float,
        y: Float,
        rocket: Rocket
    ) {

        if (
            rocket.velocity <= 0f &&
            currentPhase !=
            GameState.GamePhase.STAGE_1_ACTIVE &&
            currentPhase !=
            GameState.GamePhase.STAGE_2_ACTIVE &&
            currentPhase !=
            GameState.GamePhase.STAGE_3_ACTIVE
        ) {
            return
        }

        val power =
            when (
                currentPhase
            ) {

                GameState.GamePhase.STAGE_1_ACTIVE ->
                    0.75f

                GameState.GamePhase.STAGE_2_ACTIVE ->
                    1.05f

                GameState.GamePhase.STAGE_3_ACTIVE ->
                    1.35f

                GameState.GamePhase.IN_FLIGHT ->
                    0.90f

                GameState.GamePhase.LANDING_SEQUENCE ->
                    0.35f

                else ->
                    0.25f
            }

        val pulse =
            (
                sin(
                    animationTime * 10f
                ) + 1f
                ) / 2f

        val flameLength =
            45f +
                    (
                        40f *
                                power
                        ) +
                    pulse * 18f

        val flamePath =
            Path()

        flamePath.moveTo(
            x -
                    12f,
            y
        )

        flamePath.lineTo(
            x,
            y +
                    flameLength
        )

        flamePath.lineTo(
            x +
                    12f,
            y
        )

        flamePath.close()

        paint.color =
            Color.rgb(
                255,
                120,
                20
            )

        canvas.drawPath(
            flamePath,
            paint
        )

        val innerFlame =
            Path()

        innerFlame.moveTo(
            x -
                    7f,
            y
        )

        innerFlame.lineTo(
            x,
            y +
                    flameLength * 0.68f
        )

        innerFlame.lineTo(
            x +
                    7f,
            y
        )

        innerFlame.close()

        paint.color =
            Color.rgb(
                255,
                235,
                120
            )

        canvas.drawPath(
            innerFlame,
            paint
        )
    }

    private fun drawHud(
        canvas: Canvas,
        rocket: Rocket
    ) {

        paint.shader =
            null

        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.argb(
                150,
                5,
                12,
                25
            )

        canvas.drawRoundRect(
            16f,
            16f,
            width - 16f,
            82f,
            18f,
            18f,
            paint
        )

        paint.color =
            Color.WHITE

        paint.textSize =
            15f

        paint.textAlign =
            Paint.Align.LEFT

        canvas.drawText(
            rocket.name,
            30f,
            42f,
            paint
        )

        paint.textSize =
            12f

        paint.color =
            Color.rgb(
                180,
                210,
                230
            )

        val planetText =
            currentPlanet?.let {
                "${it.emoji} ${it.name}"
            } ?: "🪐 KOSMOS"

        canvas.drawText(
            planetText,
            30f,
            62f,
            paint
        )

        paint.textAlign =
            Paint.Align.RIGHT

        paint.color =
            Color.rgb(
                85,
                255,
                170
            )

        canvas.drawText(
            "ALT ${rocket.altitude.toInt()} km",
            width - 30f,
            42f,
            paint
        )

        paint.color =
            Color.rgb(
                255,
                215,
                90
            )

        canvas.drawText(
            "FUEL ${rocket.fuel.toInt()}",
            width - 30f,
            62f,
            paint
        )
    }
}
