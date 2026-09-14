package com.spacefrontier.game.graphics

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.spacefrontier.game.models.Rocket
import kotlin.math.sin

/**
 * Główna grafika rakiety Space Frontier.
 * Rakieta + animowany płomień + gwiazdy + efekt ruchu.
 */
class RocketView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentRocket: Rocket? = null
    private var animationTime = 0f

    fun updateRocket(rocket: Rocket) {
        currentRocket = rocket
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val rocket = currentRocket ?: return

        animationTime += 0.12f

        drawSpace(canvas)
        drawStars(canvas, rocket)
        drawRocket(canvas, rocket)

        // Animacja około 60 FPS
        postInvalidateOnAnimation()
    }

    private fun drawSpace(canvas: Canvas) {
        val gradient = LinearGradient(
            0f,
            0f,
            0f,
            height.toFloat(),
            Color.rgb(2, 7, 25),
            Color.rgb(8, 20, 48),
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
    }

    private fun drawStars(canvas: Canvas, rocket: Rocket) {
        val movement = (rocket.altitude * 2f) % height

        for (i in 0..70) {
            val x = ((i * 97) % width).toFloat()

            var y = ((i * 173) % height).toFloat()
            y += movement

            if (y > height) {
                y -= height
            }

            val size = when (i % 4) {
                0 -> 1.5f
                1 -> 2f
                2 -> 2.5f
                else -> 1f
            }

            paint.color = if (i % 7 == 0) {
                Color.rgb(120, 190, 255)
            } else {
                Color.WHITE
            }

            canvas.drawCircle(x, y, size, paint)
        }
    }

    private fun drawRocket(canvas: Canvas, rocket: Rocket) {

        val centerX = width / 2f

        /*
         * Rakieta pozostaje na ekranie.
         * Przy większej wysokości przesuwa się stopniowo,
         * a tło zaczyna przewijać się mocniej.
         */
        val normalizedAltitude =
            (rocket.altitude / 250f).coerceIn(0f, 1f)

        val centerY =
            height * 0.70f -
                    normalizedAltitude * height * 0.38f

        val rocketWidth =
            (width * 0.11f).coerceIn(70f, 115f)

        val rocketHeight = rocketWidth * 2.25f

        // Delikatne kołysanie rakiety
        val sway =
            sin(animationTime * 0.8f) *
                    if (rocket.velocity > 0f) 2.5f else 0.5f

        canvas.save()

        canvas.rotate(
            sway.toFloat(),
            centerX,
            centerY
        )

        // Płomień
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

        drawAltitudeIndicator(
            canvas,
            rocket
        )
    }

    private fun drawRocketBody(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        widthRocket: Float,
        heightRocket: Float,
        stage: Int
    ) {

        val bodyWidth = widthRocket * 0.46f
        val bodyHeight = heightRocket * 0.55f

        val top = centerY - heightRocket * 0.40f
        val bottom = top + bodyHeight

        // Glow rakiety
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(55, 80, 170, 255)

        canvas.drawOval(
            centerX - widthRocket * 0.43f,
            top - 15f,
            centerX + widthRocket * 0.43f,
            bottom + 20f,
            paint
        )

        // Kadłub
        val bodyRect = RectF(
            centerX - bodyWidth / 2f,
            top,
            centerX + bodyWidth / 2f,
            bottom
        )

        val bodyGradient = LinearGradient(
            bodyRect.left,
            0f,
            bodyRect.right,
            0f,
            Color.rgb(115, 125, 140),
            Color.WHITE,
            Shader.TileMode.CLAMP
        )

        paint.shader = bodyGradient
        canvas.drawRoundRect(
            bodyRect,
            18f,
            18f,
            paint
        )
        paint.shader = null

        // Nos rakiety
        val nose = Path()

        nose.moveTo(
            centerX,
            centerY - heightRocket * 0.48f
        )

        nose.lineTo(
            centerX - bodyWidth / 2f,
            top + 28f
        )

        nose.lineTo(
            centerX + bodyWidth / 2f,
            top + 28f
        )

        nose.close()

        paint.color = when (stage) {
            1 -> Color.rgb(220, 45, 45)
            2 -> Color.rgb(245, 120, 25)
            3 -> Color.rgb(70, 140, 255)
            else -> Color.rgb(220, 45, 45)
        }

        canvas.drawPath(nose, paint)

        // Czerwone pasy
        paint.color = Color.rgb(205, 35, 35)

        canvas.drawRect(
            bodyRect.left,
            top + bodyHeight * 0.20f,
            bodyRect.right,
            top + bodyHeight * 0.27f,
            paint
        )

        canvas.drawRect(
            bodyRect.left,
            top + bodyHeight * 0.72f,
            bodyRect.right,
            top + bodyHeight * 0.79f,
            paint
        )

        // Okno
        paint.color = Color.rgb(15, 25, 45)

        canvas.drawCircle(
            centerX,
            top + bodyHeight * 0.38f,
            bodyWidth * 0.30f,
            paint
        )

        paint.color = Color.rgb(50, 180, 255)

        canvas.drawCircle(
            centerX,
            top + bodyHeight * 0.38f,
            bodyWidth * 0.22f,
            paint
        )

        // Błysk w oknie
        paint.color = Color.argb(190, 220, 250, 255)

        canvas.drawCircle(
            centerX - bodyWidth * 0.08f,
            top + bodyHeight * 0.32f,
            bodyWidth * 0.07f,
            paint
        )

        // Lewe skrzydło
        val leftWing = Path()

        leftWing.moveTo(
            bodyRect.left,
            bottom - bodyHeight * 0.28f
        )

        leftWing.lineTo(
            bodyRect.left - widthRocket * 0.36f,
            bottom + 5f
        )

        leftWing.lineTo(
            bodyRect.left,
            bottom - bodyHeight * 0.02f
        )

        leftWing.close()

        paint.color = Color.rgb(35, 95, 175)
        canvas.drawPath(leftWing, paint)

        // Prawe skrzydło
        val rightWing = Path()

        rightWing.moveTo(
            bodyRect.right,
            bottom - bodyHeight * 0.28f
        )

        rightWing.lineTo(
            bodyRect.right + widthRocket * 0.36f,
            bottom + 5f
        )

        rightWing.lineTo(
            bodyRect.right,
            bottom - bodyHeight * 0.02f
        )

        rightWing.close()

        canvas.drawPath(rightWing, paint)

        // Dysza
        paint.color = Color.rgb(45, 50, 60)

        canvas.drawRect(
            centerX - bodyWidth * 0.28f,
            bottom - 5f,
            centerX + bodyWidth * 0.28f,
            bottom + 16f,
            paint
        )

        // Stage indicator na rakiecie
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = bodyWidth * 0.22f
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            "S$stage",
            centerX,
            bottom - bodyHeight * 0.08f,
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
            (sin(animationTime * 4f) + 1f) / 2f

        val flameLength =
            rocketWidth *
                    (0.65f + pulse * 0.45f)

        // Zewnętrzny pomarańczowy płomień
        val outer = Path()

        outer.moveTo(
            centerX - rocketWidth * 0.20f,
            topY
        )

        outer.quadTo(
            centerX - rocketWidth * 0.30f,
            topY + flameLength * 0.50f,
            centerX,
            topY + flameLength
        )

        outer.quadTo(
            centerX + rocketWidth * 0.30f,
            topY + flameLength * 0.50f,
            centerX + rocketWidth * 0.20f,
            topY
        )

        outer.close()

        paint.color = Color.rgb(255, 90, 10)
        canvas.drawPath(outer, paint)

        // Żółty środek
        val inner = Path()

        inner.moveTo(
            centerX - rocketWidth * 0.11f,
            topY
        )

        inner.quadTo(
            centerX - rocketWidth * 0.16f,
            topY + flameLength * 0.45f,
            centerX,
            topY + flameLength * 0.75f
        )

        inner.quadTo(
            centerX + rocketWidth * 0.16f,
            topY + flameLength * 0.45f,
            centerX + rocketWidth * 0.11f,
            topY
        )

        inner.close()

        paint.color = Color.YELLOW
        canvas.drawPath(inner, paint)

        // Biały rdzeń
        paint.color = Color.WHITE

        canvas.drawOval(
            centerX - rocketWidth * 0.055f,
            topY + 2f,
            centerX + rocketWidth * 0.055f,
            topY + flameLength * 0.42f,
            paint
        )
    }

    private fun drawAltitudeIndicator(
        canvas: Canvas,
        rocket: Rocket
    ) {

        val padding = 20f

        paint.color = Color.argb(190, 0, 0, 0)

        canvas.drawRoundRect(
            padding,
            15f,
            width - padding,
            58f,
            15f,
            15f,
            paint
        )

        paint.color = Color.rgb(100, 220, 255)
        paint.textSize = 18f
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            "WYSOKOŚĆ  ${rocket.altitude.toInt()} km",
            padding + 14f,
            38f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT

        paint.color = Color.WHITE

        canvas.drawText(
            "STAGE ${rocket.stage}/3",
            width - padding - 14f,
            38f,
            paint
        )
    }
}
