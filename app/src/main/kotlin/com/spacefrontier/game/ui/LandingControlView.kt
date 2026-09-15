package com.spacefrontier.game.ui

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import com.spacefrontier.game.logic.GameEngine
import com.spacefrontier.game.models.GameState
import kotlin.math.max
import kotlin.math.min

class LandingControlView(
    context: Context
) : View(context) {

    private val panelPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val borderPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val titlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val buttonPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var phase:
        GameState.GamePhase? = null

    private var velocity =
        0f

    private var altitude =
        0f

    private var brakePower =
        0f

    private var parachuteOpened =
        false

    init {

        setLayerType(
            View.LAYER_TYPE_SOFTWARE,
            null
        )

        titlePaint.typeface =
            Typeface.DEFAULT_BOLD

        textPaint.typeface =
            Typeface.DEFAULT_BOLD

        buttonPaint.typeface =
            Typeface.DEFAULT_BOLD

        borderPaint.style =
            Paint.Style.STROKE

        borderPaint.strokeWidth =
            4f

        isClickable = true

        post(
            updateRunnable
        )
    }

    private val updateRunnable =
        object : Runnable {

            override fun run() {

                updateState()

                invalidate()

                postDelayed(
                    this,
                    80
                )
            }
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

        if (
            phase !=
            GameState.GamePhase.LANDING_SEQUENCE
        ) {

            brakePower = 0f
            parachuteOpened = false
        }
    }

    private fun isVisibleInGame():
        Boolean {

        return phase ==
            GameState.GamePhase.LANDING_SEQUENCE
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        if (
            !isVisibleInGame()
        ) {

            return
        }

        val centerX =
            width / 2f

        val centerY =
            height - 95f

        drawControlPanel(
            canvas,
            centerX,
            centerY
        )
    }

    private fun drawControlPanel(
        canvas: Canvas,
        centerX: Float,
        centerY: Float
    ) {

        val left =
            18f

        val right =
            width - 18f

        val top =
            centerY - 65f

        val bottom =
            centerY + 65f

        panelPaint.color =
            Color.argb(
                235,
                5,
                18,
                32
            )

        panelPaint.setShadowLayer(
            14f,
            0f,
            4f,
            Color.BLACK
        )

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            20f,
            20f,
            panelPaint
        )

        panelPaint.clearShadowLayer()

        borderPaint.color =
            if (
                velocity > 35f
            ) {

                Color.rgb(
                    255,
                    70,
                    60
                )

            } else {

                Color.rgb(
                    70,
                    210,
                    255
                )
            }

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            20f,
            20f,
            borderPaint
        )

        titlePaint.textAlign =
            Paint.Align.CENTER

        titlePaint.textSize =
            17f

        titlePaint.color =
            Color.WHITE

        canvas.drawText(
            "STEROWANIE LĄDOWANIEM",
            centerX,
            top + 27f,
            titlePaint
        )

        drawVelocityBar(
            canvas,
            centerX,
            top + 47f
        )

        drawBrakeButton(
            canvas,
            centerX - 85f,
            top + 92f
        )

        drawParachuteButton(
            canvas,
            centerX + 85f,
            top + 92f
        )
    }

    private fun drawVelocityBar(
        canvas: Canvas,
        centerX: Float,
        y: Float
    ) {

        val barWidth =
            min(
                width - 70f,
                430f
            )

        val left =
            centerX - barWidth / 2f

        val right =
            centerX + barWidth / 2f

        val progress =
            min(
                velocity / 45f,
                1f
            )

        panelPaint.color =
            Color.rgb(
                35,
                55,
                70
            )

        canvas.drawRoundRect(
            left,
            y,
            right,
            y + 13f,
            7f,
            7f,
            panelPaint
        )

        buttonPaint.color =
            if (
                velocity <= 35f
            ) {

                Color.rgb(
                    70,
                    255,
                    150
                )

            } else {

                Color.rgb(
                    255,
                    70,
                    60
                )
            }

        canvas.drawRoundRect(
            left,
            y,
            left +
                barWidth *
                progress,
            y + 13f,
            7f,
            7f,
            buttonPaint
        )

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            13f

        textPaint.color =
            Color.WHITE

        canvas.drawText(
            "${velocity.toInt()} km/h  •  BEZPIECZNA ≤ 35",
            centerX,
            y - 5f,
            textPaint
        )
    }

    private fun drawBrakeButton(
        canvas: Canvas,
        x: Float,
        y: Float
    ) {

        val pressed =
            brakePower > 0f

        buttonPaint.color =
            if (pressed) {

                Color.rgb(
                    40,
                    190,
                    255
                )

            } else {

                Color.rgb(
                    15,
                    90,
                    130
                )
            }

        canvas.drawRoundRect(
            x - 70f,
            y - 24f,
            x + 70f,
            y + 24f,
            14f,
            14f,
            buttonPaint
        )

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            15f

        textPaint.color =
            Color.WHITE

        canvas.drawText(
            if (pressed)
                "◀ HAMULEC AKTYWNY"
            else
                "◀ HAMUJ",
            x,
            y + 6f,
            textPaint
        )
    }

    private fun drawParachuteButton(
        canvas: Canvas,
        x: Float,
        y: Float
    ) {

        buttonPaint.color =
            if (parachuteOpened) {

                Color.rgb(
                    40,
                    170,
                    90
                )

            } else {

                Color.rgb(
                    150,
                    90,
                    20
                )
            }

        canvas.drawRoundRect(
            x - 70f,
            y - 24f,
            x + 70f,
            y + 24f,
            14f,
            14f,
            buttonPaint
        )

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            15f

        textPaint.color =
            Color.WHITE

        canvas.drawText(
            if (parachuteOpened)
                "🪂 SPADOCHRON OTW."
            else
                "🪂 SPADOCHRON",
            x,
            y + 6f,
            textPaint
        )
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (
            !isVisibleInGame()
        ) {

            return false
        }

        if (
            event.action !=
            MotionEvent.ACTION_DOWN &&
            event.action !=
            MotionEvent.ACTION_MOVE &&
            event.action !=
            MotionEvent.ACTION_UP
        ) {

            return true
        }

        val centerX =
            width / 2f

        val buttonY =
            height - 3f

        val brakeX =
            centerX - 85f

        val parachuteX =
            centerX + 85f

        val brakePressed =
            event.x >= brakeX - 80f &&
            event.x <= brakeX + 80f &&
            event.y >= buttonY - 150f &&
            event.y <= buttonY - 70f

        val parachutePressed =
            event.x >= parachuteX - 80f &&
            event.x <= parachuteX + 80f &&
            event.y >= buttonY - 150f &&
            event.y <= buttonY - 70f

        when {

            brakePressed -> {

                brakePower =
                    if (
                        event.action ==
                        MotionEvent.ACTION_UP
                    ) {
                        0f
                    } else {
                        1f
                    }

                invalidate()

                return true
            }

            parachutePressed &&
                event.action ==
                MotionEvent.ACTION_DOWN -> {

                parachuteOpened =
                    true

                invalidate()

                return true
            }
        }

        return true
    }

    override fun onDetachedFromWindow() {

        removeCallbacks(
            updateRunnable
        )

        super.onDetachedFromWindow()
    }
}
