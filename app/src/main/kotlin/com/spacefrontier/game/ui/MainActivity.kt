package com.spacefrontier.game.graphics

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.spacefrontier.game.models.GameState
import com.spacefrontier.game.models.Planet
import com.spacefrontier.game.models.Rocket
import kotlin.math.sin

class RocketView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentRocket: Rocket? = null
    private var currentPlanet: Planet? = null
    private var currentPhase = GameState.GamePhase.AWAITING_FIRST_TAP
    private var animationTime = 0f

    fun updateRocket(rocket: Rocket) {
        currentRocket = rocket
        invalidate()
    }

    fun updateScene(
        rocket: Rocket,
        planet: Planet?,
        phase: GameState.GamePhase
    ) {
        currentRocket = rocket
        currentPlanet = planet
        currentPhase = phase
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val rocket = currentRocket ?: return

        animationTime += 0.08f

        drawSpaceBackground(canvas, rocket)
        drawStars(canvas, rocket)
        drawAsteroids(canvas, rocket)

        if (
            currentPhase == GameState.GamePhase.LANDING_SEQUENCE ||
            currentPhase == GameState.GamePhase.LANDED_SUCCESS ||
            currentPhase == GameState.GamePhase.LANDED_FAILED
        ) {
            drawLandingPlanet(canvas)
        }

        drawRocket(canvas, rocket)

        postInvalidateOnAnimation()
    }

    private fun drawSpaceBackground(
        canvas: Canvas,
        rocket: Rocket
    ) {
        val gradient = LinearGradient(
            0f,
            0f,
            0f,
            height.toFloat(),
            Color.rgb(1, 5, 20),
            Color.rgb(5, 18, 45),
            Shader.TileMode.CLAMP
        )

        paint.shader = gradient

        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            paint
        )

        paint.shader = null

        paint.color = Color.argb(35, 50, 100, 220)

        canvas.drawCircle(
            width * 0.18f,
            height * 0.25f,
            width * 0.28f,
            paint
        )

        paint.color = Color.argb(25, 180, 60, 220)

        canvas.drawCircle(
            width * 0.82f,
            height * 0.68f,
            width * 0.32f,
            paint
        )
    }

    private fun drawStars(
        canvas: Canvas,
        rocket: Rocket
    ) {
        val speed = rocket.velocity.coerceAtLeast(0f)

        val movement =
            (animationTime * (8f + speed * 0.12f)) % height

        for (i in 0..95) {

            val x =
                ((i * 97 + 31) % width).toFloat()

            var y =
                ((i * 173 + 47) % height).toFloat() +
                        movement

            if (y > height) {
                y -= height
            }

            val size = when (i % 5) {
                0 -> 2.4f
                1 -> 1.5f
                2 -> 1.0f
                3 -> 2.0f
                else -> 0.8f
            }

            paint.color = when (i % 9) {
                0 -> Color.rgb(130, 210, 255)
                1 -> Color.rgb(190, 150, 255)
                else -> Color.WHITE
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
            Color.argb(220, 220, 245, 255)

        canvas.drawCircle(
            x,
            y,
            3f,
            paint
        )

        paint.strokeWidth = 1.5f

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
            (animationTime *
                    (5f + rocket.velocity * 0.05f)) %
                    (height + 250f)

        val asteroidData = listOf(
            Triple(0.14f, 0.20f, 34f),
            Triple(0.83f, 0.34f, 25f),
            Triple(0.10f, 0.68f, 20f),
            Triple(0.88f, 0.78f, 42f),
            Triple(0.28f, 0.48f, 15f),
            Triple(0.70f, 0.13f, 18f)
        )

        asteroidData.forEachIndexed { index, data ->

            val x =
                width * data.first

            var y =
                height * data.second +
                        movement *
                        if (index % 2 == 0) 1f else -1f

            if (y > height + 80f) {
                y -= height + 160f
            }

            if (y < -80f) {
                y += height + 160f
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
        val path = Path()
        val points = 9

        for (i in 0 until points) {

            val angle =
                Math.PI * 2.0 * i / points

            val variation =
                0.78f +
                        ((i * 17 + index * 11) % 30) / 100f

            val px =
                x +
                        (kotlin.math.cos(angle) *
                                radius *
                                variation).toFloat()

            val py =
                y +
                        (kotlin.math.sin(angle) *
                                radius *
                                variation).toFloat()

            if (i == 0) {
                path.moveTo(px, py)
            } else {
                path.lineTo(px, py)
            }
        }

        path.close()

        paint.color =
            Color.rgb(65, 68, 78)

        canvas.drawPath(
            path,
            paint
        )

        paint.color =
            Color.rgb(105, 108, 120)

        canvas.drawCircle(
            x - radius * 0.25f,
            y - radius * 0.25f,
            radius * 0.28f,
            paint
        )

        paint.color =
            Color.rgb(42, 44, 52)

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

        paint.color =
            Color.argb(80, 0, 0, 0)

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth = 2f

        canvas.drawPath(
            path,
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
            (rocket.altitude / 300f)
                .coerceIn(0f, 1f)

        val centerY =
            height * 0.68f -
                    altitudeProgress *
                    height * 0.36f

        val rocketWidth =
            (width * 0.12f)
                .coerceIn(72f, 120f)

        val rocketHeight =
            rocketWidth * 2.25f

        val sway =
            sin(animationTime * 2.2f) *
                    if (rocket.velocity > 0f) 2.5f else 0.5f

        canvas.save()

        canvas.rotate(
            sway.toFloat(),
            centerX,
            centerY
        )

        if (rocket.velocity > 0f) {
            drawEngineFlame(
                canvas,
                centerX,
                centerY + rocketHeight * 0.43f,
                rocketWidth
            )
        }

        drawRocketBody(
            canvas,
            centerX,
            centerY,
            rocketWidth,
            rocketHeight,
            rocket.stage
        )

        canvas.restore()

        drawFlightInfo(
            canvas,
            rocket
        )
    }

    private fun drawRocketBody(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        rocketWidth: Float,
        rocketHeight: Float,
        stage: Int
    ) {
        val bodyWidth =
            rocketWidth * 0.46f

        val bodyHeight =
            rocketHeight * 0.55f

        val top =
            centerY - rocketHeight * 0.40f

        val bottom =
            top + bodyHeight

        paint.color =
            Color.argb(45, 80, 180, 255)

        canvas.drawOval(
            centerX - rocketWidth * 0.48f,
            top - 20f,
            centerX + rocketWidth * 0.48f,
            bottom + 25f,
            paint
        )

        val body = RectF(
            centerX - bodyWidth / 2f,
            top,
            centerX + bodyWidth / 2f,
            bottom
        )

        val bodyGradient = LinearGradient(
            body.left,
            0f,
            body.right,
            0f,
            Color.rgb(90, 100, 115),
            Color.WHITE,
            Shader.TileMode.CLAMP
        )

        paint.shader =
            bodyGradient

        canvas.drawRoundRect(
            body,
            18f,
            18f,
            paint
        )

        paint.shader = null

        val nose = Path()

        nose.moveTo(
            centerX,
            centerY - rocketHeight * 0.48f
        )

        nose.lineTo(
            body.left,
            top + 30f
        )

        nose.lineTo(
            body.right,
            top + 30f
        )

        nose.close()

        paint.color = when (stage) {
            1 -> Color.rgb(225, 45, 45)
            2 -> Color.rgb(255, 120, 20)
            3 -> Color.rgb(55, 130, 255)
            else -> Color.rgb(225, 45, 45)
        }

        canvas.drawPath(
            nose,
            paint
        )

        paint.color =
            Color.rgb(205, 35, 35)

        canvas.drawRect(
            body.left,
            top + bodyHeight * 0.20f,
            body.right,
            top + bodyHeight * 0.27f,
            paint
        )

        canvas.drawRect(
            body.left,
            top + bodyHeight * 0.72f,
            body.right,
            top + bodyHeight * 0.79f,
            paint
        )

        paint.color =
            Color.rgb(8, 18, 35)

        canvas.drawCircle(
            centerX,
            top + bodyHeight * 0.38f,
            bodyWidth * 0.30f,
            paint
        )

        paint.color =
            Color.rgb(30, 170, 255)

        canvas.drawCircle(
            centerX,
            top + bodyHeight * 0.38f,
            bodyWidth * 0.21f,
            paint
        )

        paint.color = Color.WHITE

        canvas.drawCircle(
            centerX - bodyWidth * 0.07f,
            top + bodyHeight * 0.32f,
            bodyWidth * 0.06f,
            paint
        )

        paint.color =
            Color.rgb(30, 85, 170)

        val leftWing = Path()

        leftWing.moveTo(
            body.left,
            bottom - bodyHeight * 0.30f
        )

        leftWing.lineTo(
            body.left - rocketWidth * 0.38f,
            bottom + 10f
        )

        leftWing.lineTo(
            body.left,
            bottom - bodyHeight * 0.02f
        )

        leftWing.close()

        canvas.drawPath(
            leftWing,
            paint
        )

        val rightWing = Path()

        rightWing.moveTo(
            body.right,
            bottom - bodyHeight * 0.30f
        )

        rightWing.lineTo(
            body.right + rocketWidth * 0.38f,
            bottom + 10f
        )

        rightWing.lineTo(
            body.right,
            bottom - bodyHeight * 0.02f
        )

        rightWing.close()

        canvas.drawPath(
            rightWing,
            paint
        )

        paint.color =
            Color.rgb(40, 45, 55)

        canvas.drawRect(
            centerX - bodyWidth * 0.27f,
            bottom - 4f,
            centerX + bodyWidth * 0.27f,
            bottom + 16f,
            paint
        )
    }

    private fun drawEngineFlame(
        canvas: Canvas,
        centerX: Float,
        topY: Float,
        rocketWidth: Float
    ) {
        val pulse =
            (sin(animationTime * 5f) + 1f) / 2f

        val length =
            rocketWidth *
                    (0.70f + pulse * 0.55f)

        val outer = Path()

        outer.moveTo(
            centerX - rocketWidth * 0.20f,
            topY
        )

        outer.quadTo(
            centerX - rocketWidth * 0.32f,
            topY + length * 0.45f,
            centerX,
            topY + length
        )

        outer.quadTo(
            centerX + rocketWidth * 0.32f,
            topY + length * 0.45f,
            centerX + rocketWidth * 0.20f,
            topY
        )

        outer.close()

        paint.color =
            Color.rgb(255, 75, 5)

        canvas.drawPath(
            outer,
            paint
        )

        val inner = Path()

        inner.moveTo(
            centerX - rocketWidth * 0.11f,
            topY
        )

        inner.quadTo(
            centerX - rocketWidth * 0.16f,
            topY + length * 0.42f,
            centerX,
            topY + length * 0.76f
        )

        inner.quadTo(
            centerX + rocketWidth * 0.16f,
            topY + length * 0.42f,
            centerX + rocketWidth * 0.11f,
            topY
        )

        inner.close()

        paint.color =
            Color.YELLOW

        canvas.drawPath(
            inner,
            paint
        )

        paint.color =
            Color.WHITE

        canvas.drawOval(
            centerX - rocketWidth * 0.05f,
            topY,
            centerX + rocketWidth * 0.05f,
            topY + length * 0.42f,
            paint
        )
    }

    private fun drawFlightInfo(
        canvas: Canvas,
        rocket: Rocket
    ) {
        paint.color =
            Color.argb(185, 0, 0, 0)

        canvas.drawRoundRect(
            16f,
            14f,
            width - 16f,
            60f,
            14f,
            14f,
            paint
        )

        paint.color =
            Color.rgb(100, 220, 255)

        paint.textSize = 17f
        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textAlign =
            Paint.Align.LEFT

        canvas.drawText(
            "WYSOKOŚĆ ${rocket.altitude.toInt()} km",
            30f,
            42f,
            paint
        )

        paint.textAlign =
            Paint.Align.RIGHT

        paint.color = Color.WHITE

        canvas.drawText(
            "STAGE ${rocket.stage}/3",
            width - 30f,
            42f,
            paint
        )
    }

    private fun drawLandingPlanet(
        canvas: Canvas
    ) {
        val planet =
            currentPlanet ?: return

        val centerX =
            width / 2f

        val centerY =
            height * 0.88f

        val radius =
            width * 0.42f

        val planetColor =
            when (planet.name) {
                "Luna" ->
                    Color.rgb(145, 145, 155)

                "Mars" ->
                    Color.rgb(175, 65, 40)

                "Europa" ->
                    Color.rgb(170, 205, 225)

                "Titan" ->
                    Color.rgb(190, 130, 55)

                "Neptune" ->
                    Color.rgb(45, 95, 210)

                else ->
                    Color.rgb(70, 120, 180)
            }

        paint.color =
            Color.argb(55, 80, 170, 255)

        canvas.drawCircle(
            centerX,
            centerY,
            radius * 1.12f,
            paint
        )

        paint.shader =
            RadialGradient(
                centerX - radius * 0.35f,
                centerY - radius * 0.35f,
                radius,
                Color.WHITE,
                planetColor,
                Shader.TileMode.CLAMP
            )

        canvas.drawCircle(
            centerX,
            centerY,
            radius,
            paint
        )

        paint.shader = null

        paint.color =
            Color.argb(45, 0, 0, 0)

        canvas.drawCircle(
            centerX - radius * 0.35f,
            centerY - radius * 0.10f,
            radius * 0.10f,
            paint
        )

        canvas.drawCircle(
            centerX + radius * 0.20f,
            centerY - radius * 0.25f,
            radius * 0.07f,
            paint
        )

        canvas.drawCircle(
            centerX + radius * 0.35f,
            centerY + radius * 0.18f,
            radius * 0.13f,
            paint
        )

        if (
            currentPhase ==
            GameState.GamePhase.LANDING_SEQUENCE
        ) {
            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth = 5f

            paint.color =
                Color.rgb(80, 255, 150)

            canvas.drawOval(
                centerX - radius * 0.45f,
                centerY - radius * 0.10f,
                centerX + radius * 0.45f,
                centerY + radius * 0.10f,
                paint
            )

            paint.style =
                Paint.Style.FILL
        }

        paint.color = Color.WHITE
        paint.textAlign =
            Paint.Align.CENTER

        paint.textSize = 24f
        paint.typeface =
            Typeface.DEFAULT_BOLD

        canvas.drawText(
            planet.name.uppercase(),
            centerX,
            height - 25f,
            paint
        )
    }
}
