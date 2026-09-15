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
import com.spacefrontier.game.logic.GameEngine
import com.spacefrontier.game.models.GameState
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class LandingEffectsView(
    context: Context
) : View(context) {

    private val handler =
        Handler(Looper.getMainLooper())

    private val parachutePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val parachuteLinePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val trajectoryPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val landingZonePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val glowPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val particlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var phase:
        GameState.GamePhase? = null

    private var altitude =
        0f

    private var velocity =
        0f

    private var animationTime =
        0f

    private val particles =
        ArrayList<Particle>()

    private data class Particle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var life: Float,
        var size: Float
    )

    private val updateRunnable =
        object : Runnable {

            override fun run() {

                updateState()

                animationTime +=
                    0.08f

                updateParticles()

                invalidate()

                handler.postDelayed(
                    this,
                    40
                )
            }
        }

    init {

        setLayerType(
            View.LAYER_TYPE_SOFTWARE,
            null
        )

        parachutePaint.style =
            Paint.Style.FILL

        parachuteLinePaint.style =
            Paint.Style.STROKE

        parachuteLinePaint.strokeWidth =
            3f

        trajectoryPaint.style =
            Paint.Style.STROKE

        trajectoryPaint.strokeWidth =
            4f

        landingZonePaint.style =
            Paint.Style.FILL

        glowPaint.style =
            Paint.Style.FILL

        particlePaint.style =
            Paint.Style.FILL

        textPaint.typeface =
            Typeface.DEFAULT_BOLD

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

        altitude =
            state.rocket.altitude

        velocity =
            state.rocket.velocity
    }

    private fun updateParticles() {

        val currentPhase =
            phase

        if (
            currentPhase !=
            GameState.GamePhase.LANDING_SEQUENCE &&
            currentPhase !=
            GameState.GamePhase.LANDED_SUCCESS &&
            currentPhase !=
            GameState.GamePhase.LANDED_FAILED
        ) {

            particles.clear()

            return
        }

        for (
            particle in particles
        ) {

            particle.x +=
                particle.vx

            particle.y +=
                particle.vy

            particle.vy +=
                0.08f

            particle.life -=
                0.035f
        }

        particles.removeAll {
            it.life <= 0f
        }

        if (
            currentPhase ==
            GameState.GamePhase.LANDING_SEQUENCE
        ) {

            repeat(2) {

                if (
                    particles.size < 45
                ) {

                    particles.add(
                        createParticle()
                    )
                }
            }
        }
    }

    private fun createParticle():
        Particle {

        val centerX =
            width / 2f

        val centerY =
            height * 0.72f

        val angle =
            Random.nextFloat() *
                Math.PI.toFloat() *
                2f

        val speed =
            0.5f +
                Random.nextFloat() * 2.5f

        return Particle(
            x =
                centerX +
                    Random.nextFloat() * 100f -
                    50f,
            y =
                centerY +
                    Random.nextFloat() * 20f,
            vx =
                cos(angle) * speed,
            vy =
                sin(angle) * speed -
                    1.5f,
            life =
                0.6f +
                    Random.nextFloat() * 0.8f,
            size =
                2f +
                    Random.nextFloat() * 5f
        )
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        val currentPhase =
            phase

        if (
            currentPhase !=
            GameState.GamePhase.LANDING_SEQUENCE &&
            currentPhase !=
            GameState.GamePhase.LANDED_SUCCESS &&
            currentPhase !=
            GameState.GamePhase.LANDED_FAILED
        ) {

            return
        }

        val centerX =
            width / 2f

        val rocketY =
            height * 0.43f

        when (currentPhase) {

            GameState.GamePhase.LANDING_SEQUENCE -> {

                drawLandingZone(
                    canvas,
                    centerX
                )

                drawTrajectory(
                    canvas,
                    centerX,
                    rocketY
                )

                drawParachute(
                    canvas,
                    centerX,
                    rocketY
                )

                drawParticles(
                    canvas
                )

                drawVelocityIndicator(
                    canvas,
                    centerX
                )
            }

            GameState.GamePhase.LANDED_SUCCESS -> {

                drawSuccessEffect(
                    canvas,
                    centerX
                )
            }

            GameState.GamePhase.LANDED_FAILED -> {

                drawFailureEffect(
                    canvas,
                    centerX
                )
            }

            else -> Unit
        }
    }

    private fun drawLandingZone(
        canvas: Canvas,
        centerX: Float
    ) {

        val groundY =
            height * 0.78f

        val pulse =
            (
                sin(
                    animationTime * 5f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        landingZonePaint.color =
            Color.argb(
                (
                    35f +
                        pulse * 35f
                    ).toInt(),
                40,
                255,
                120
            )

        canvas.drawOval(
            centerX - 115f,
            groundY - 14f,
            centerX + 115f,
            groundY + 14f,
            landingZonePaint
        )

        glowPaint.color =
            Color.argb(
                (
                    30f +
                        pulse * 40f
                    ).toInt(),
                50,
                255,
                140
            )

        glowPaint.setShadowLayer(
            25f,
            0f,
            0f,
            Color.rgb(
                50,
                255,
                140
            )
        )

        canvas.drawOval(
            centerX - 80f,
            groundY - 5f,
            centerX + 80f,
            groundY + 5f,
            glowPaint
        )

        glowPaint.clearShadowLayer()

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            14f

        textPaint.color =
            Color.rgb(
                90,
                255,
                160
            )

        canvas.drawText(
            "STREFA BEZPIECZNEGO LĄDOWANIA",
            centerX,
            groundY + 38f,
            textPaint
        )
    }

    private fun drawTrajectory(
        canvas: Canvas,
        centerX: Float,
        rocketY: Float
    ) {

        trajectoryPaint.color =
            Color.argb(
                110,
                120,
                220,
                255
            )

        val groundY =
            height * 0.76f

        var y =
            rocketY + 55f

        while (
            y < groundY
        ) {

            canvas.drawLine(
                centerX,
                y,
                centerX,
                y + 12f,
                trajectoryPaint
            )

            y += 25f
        }
    }

    private fun drawParachute(
        canvas: Canvas,
        centerX: Float,
        rocketY: Float
    ) {

        val open =
            altitude < 80f ||
                velocity < 42f

        if (!open) {
            return
        }

        val parachuteY =
            rocketY - 75f

        val width =
            if (altitude < 35f) {
                105f
            } else {
                85f
            }

        val height =
            55f

        parachutePaint.color =
            Color.argb(
                235,
                220,
                225,
                235
            )

        canvas.drawArc(
            centerX - width,
            parachuteY - height,
            centerX + width,
            parachuteY + height,
            180f,
            180f,
            true,
            parachutePaint
        )

        parachuteLinePaint.color =
            Color.rgb(
                235,
                240,
                255
            )

        canvas.drawLine(
            centerX - width * 0.75f,
            parachuteY + 5f,
            centerX - 25f,
            rocketY - 20f,
            parachuteLinePaint
        )

        canvas.drawLine(
            centerX + width * 0.75f,
            parachuteY + 5f,
            centerX + 25f,
            rocketY - 20f,
            parachuteLinePaint
        )

        canvas.drawLine(
            centerX,
            parachuteY + 10f,
            centerX,
            rocketY - 15f,
            parachuteLinePaint
        )

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            13f

        textPaint.color =
            Color.WHITE

        canvas.drawText(
            "🪂 SPADOCHRON",
            centerX,
            parachuteY - 65f,
            textPaint
        )
    }

    private fun drawVelocityIndicator(
        canvas: Canvas,
        centerX: Float
    ) {

        val safe =
            velocity <= 35f

        val almostSafe =
            velocity <= 45f

        val color =
            when {

                safe ->
                    Color.rgb(
                        70,
                        255,
                        150
                    )

                almostSafe ->
                    Color.rgb(
                        255,
                        210,
                        60
                    )

                else ->
                    Color.rgb(
                        255,
                        60,
                        60
                    )
            }

        glowPaint.color =
            Color.argb(
                180,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )

        glowPaint.setShadowLayer(
            18f,
            0f,
            0f,
            color
        )

        canvas.drawCircle(
            centerX,
            height * 0.90f,
            9f,
            glowPaint
        )

        glowPaint.clearShadowLayer()

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            15f

        textPaint.color =
            color

        val label =
            when {

                safe ->
                    "PRĘDKOŚĆ BEZPIECZNA"

                almostSafe ->
                    "HAMOWANIE"

                else ->
                    "ZA DUŻA PRĘDKOŚĆ"
            }

        canvas.drawText(
            label,
            centerX,
            height * 0.95f,
            textPaint
        )
    }

    private fun drawParticles(
        canvas: Canvas
    ) {

        for (
            particle in particles
        ) {

            val alpha =
                (
                    particle.life *
                        255f
                    ).toInt()
                    .coerceIn(
                        0,
                        255
                    )

            particlePaint.color =
                Color.argb(
                    alpha,
                    150,
                    220,
                    255
                )

            canvas.drawCircle(
                particle.x,
                particle.y,
                particle.size,
                particlePaint
            )
        }
    }

    private fun drawSuccessEffect(
        canvas: Canvas,
        centerX: Float
    ) {

        val pulse =
            (
                sin(
                    animationTime * 8f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        glowPaint.color =
            Color.argb(
                (
                    30f +
                        pulse * 70f
                    ).toInt(),
                60,
                255,
                150
            )

        glowPaint.setShadowLayer(
            45f,
            0f,
            0f,
            Color.rgb(
                60,
                255,
                150
            )
        )

        canvas.drawCircle(
            centerX,
            height * 0.76f,
            45f +
                pulse * 20f,
            glowPaint
        )

        glowPaint.clearShadowLayer()

        repeat(12) { index ->

            val angle =
                animationTime * 1.5f +
                    index *
                    (Math.PI.toFloat() * 2f / 12f)

            val radius =
                60f +
                    pulse * 45f

            val x =
                centerX +
                    cos(angle) * radius

            val y =
                height * 0.76f +
                    sin(angle) * radius

            particlePaint.color =
                Color.rgb(
                    80,
                    255,
                    160
                )

            canvas.drawCircle(
                x,
                y,
                4f,
                particlePaint
            )
        }
    }

    private fun drawFailureEffect(
        canvas: Canvas,
        centerX: Float
    ) {

        val pulse =
            (
                sin(
                    animationTime * 9f
                ) * 0.5f +
                    0.5f
            ).toFloat()

        glowPaint.color =
            Color.argb(
                (
                    35f +
                        pulse * 70f
                    ).toInt(),
                255,
                40,
                40
            )

        glowPaint.setShadowLayer(
            40f,
            0f,
            0f,
            Color.RED
        )

        canvas.drawCircle(
            centerX,
            height * 0.76f,
            40f +
                pulse * 18f,
            glowPaint
        )

        glowPaint.clearShadowLayer()

        textPaint.textAlign =
            Paint.Align.CENTER

        textPaint.textSize =
            16f

        textPaint.color =
            Color.rgb(
                255,
                90,
                90
            )

        canvas.drawText(
            "⚠ AWARIA LĄDOWANIA",
            centerX,
            height * 0.87f,
            textPaint
        )
    }

    override fun onDetachedFromWindow() {

        handler.removeCallbacks(
            updateRunnable
        )

        particles.clear()

        super.onDetachedFromWindow()
    }
}
