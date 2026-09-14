package com.spacefrontier.game.graphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.spacefrontier.game.models.Rocket
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom View do rysowania rakiety i tła gry
 */
class RocketView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentRocket: Rocket? = null
    
    fun updateRocket(rocket: Rocket) {
        currentRocket = rocket
        invalidate()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val rocket = currentRocket ?: return
        
        // Tło - gradient gwiazd
        drawStarfield(canvas)
        
        // Rysuj rakietę
        drawRocket(canvas, rocket)
    }
    
    private fun drawStarfield(canvas: Canvas) {
        paint.color = Color.parseColor("#0a0e27")
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        
        // Gwiazdy
        paint.color = Color.WHITE
        paint.strokeWidth = 2f
        
        for (i in 0..50) {
            val x = ((i * 73) % width).toFloat()
            val y = ((i * 137) % height).toFloat()
            canvas.drawPoint(x, y, paint)
        }
    }
    
    private fun drawRocket(canvas: Canvas, rocket: Rocket) {
        val centerX = width / 2f
        val baseY = height / 2f
        
        // Pozycja rakiety zależy od wysokości
        val rocketY = baseY - (rocket.altitude * 10) // skalowanie do ekranu
        
        // Rysuj płomień silnika jeśli prędkość > 0
        if (rocket.velocity > 0) {
            drawEngineFlame(canvas, centerX, rocketY + 60)
        }
        
        // Rysuj kadłub rakiety
        drawRocketBody(canvas, centerX, rocketY)
        
        // Rysuj przenośnik lotu (altitude indicator)
        drawAltitudeIndicator(canvas, rocket)
    }
    
    private fun drawRocketBody(canvas: Canvas, centerX: Float, y: Float) {
        paint.color = Color.parseColor("#ff6b00")  // Pomarańczowy
        paint.style = Paint.Style.FILL
        
        // Kadłub
        canvas.drawRect(centerX - 15, y, centerX + 15, y + 60, paint)
        
        // Nos rakiety
        paint.color = Color.parseColor("#ffff00")  // Żółty
        canvas.drawCircle(centerX, y - 20, 12f, paint)
        
        // Skrzydła
        paint.color = Color.parseColor("#0088ff")  // Niebieski
        canvas.drawTriangle(canvas, centerX - 15, y + 40, centerX - 35, y + 60, centerX - 15, y + 60)
        canvas.drawTriangle(canvas, centerX + 15, y + 40, centerX + 35, y + 60, centerX + 15, y + 60)
    }
    
    private fun drawEngineFlame(canvas: Canvas, centerX: Float, y: Float) {
        paint.color = Color.parseColor("#ff4400")
        paint.style = Paint.Style.FILL
        
        // Płomień
        for (i in 0..2) {
            val flameHeight = (20 - i * 5).toFloat()
            paint.color = when (i) {
                0 -> Color.parseColor("#ff4400")
                1 -> Color.parseColor("#ffaa00")
                else -> Color.parseColor("#ffff00")
            }
            canvas.drawRect(centerX - 8, y + i * 15, centerX + 8, y + i * 15 + flameHeight, paint)
        }
    }
    
    private fun drawAltitudeIndicator(canvas: Canvas, rocket: Rocket) {
        paint.color = Color.parseColor("#88ff88")
        paint.textSize = 18f
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Wysokość: ${rocket.altitude.toInt()} km", 20f, 40f, paint)
    }
    
    // Helper function to draw triangle
    private fun Canvas.drawTriangle(canvas: Canvas, x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
        val path = android.graphics.Path()
        path.moveTo(x1, y1)
        path.lineTo(x2, y2)
        path.lineTo(x3, y3)
        path.close()
        canvas.drawPath(path, paint)
    }
}
