package com.spacefrontier.game.logic

import com.spacefrontier.game.models.GameState
import com.spacefrontier.game.models.Planets

class GameEngine {

    private val state = GameState()

    private var flightTime = 0f

    init {
        choosePlanet()
    }

    fun getGameState(): GameState = state

    private fun choosePlanet() {
        state.currentPlanet = Planets.all.random()
    }

    fun handleTap() {
        when (state.gamePhase) {

            GameState.GamePhase.AWAITING_FIRST_TAP -> {
                state.gamePhase = GameState.GamePhase.STAGE_1_ACTIVE
                state.rocket.stage = 1
            }

            GameState.GamePhase.STAGE_1_ACTIVE -> {
                state.gamePhase = GameState.GamePhase.STAGE_2_ACTIVE
                state.rocket.stage = 2
            }

            GameState.GamePhase.STAGE_2_ACTIVE -> {
                state.gamePhase = GameState.GamePhase.STAGE_3_ACTIVE
                state.rocket.stage = 3
            }

            GameState.GamePhase.LANDED_SUCCESS,
            GameState.GamePhase.LANDED_FAILED -> {
                reset()
            }

            else -> Unit
        }
    }

    fun update(deltaTime: Float) {

        when (state.gamePhase) {

            GameState.GamePhase.STAGE_1_ACTIVE ->
                launch(deltaTime, 1)

            GameState.GamePhase.STAGE_2_ACTIVE ->
                launch(deltaTime, 2)

            GameState.GamePhase.STAGE_3_ACTIVE -> {
                launch(deltaTime, 3)

                if (state.rocket.altitude >= 40f) {
                    state.gamePhase = GameState.GamePhase.IN_FLIGHT
                }
            }

            GameState.GamePhase.IN_FLIGHT ->
                fly(deltaTime)

            GameState.GamePhase.LANDING_SEQUENCE ->
                landing(deltaTime)

            else -> Unit
        }
    }

    private fun launch(deltaTime: Float, stage: Int) {

        val rocket = state.rocket

        val thrust = when (stage) {
            1 -> 35f
            2 -> 55f
            else -> 80f
        }

        val fuelConsumption = when (stage) {
            1 -> 4f
            2 -> 6f
            else -> 8f
        }

        rocket.acceleration = thrust

        rocket.velocity += rocket.acceleration * deltaTime
        rocket.altitude += rocket.velocity * deltaTime

        rocket.fuel -= fuelConsumption * deltaTime
        rocket.fuel = rocket.fuel.coerceAtLeast(0f)

        if (rocket.fuel <= 0f) {
            state.gamePhase = GameState.GamePhase.LANDING_SEQUENCE
        }
    }

    private fun fly(deltaTime: Float) {

        val rocket = state.rocket
        val planet = state.currentPlanet ?: return

        flightTime += deltaTime

        rocket.acceleration = -6f
        rocket.velocity += rocket.acceleration * deltaTime
        rocket.altitude += rocket.velocity * deltaTime

        rocket.fuel -= 2f * deltaTime
        rocket.fuel = rocket.fuel.coerceAtLeast(0f)

        if (
            rocket.altitude >= planet.targetAltitude ||
            rocket.fuel <= 0f ||
            flightTime >= 12f
        ) {
            state.gamePhase = GameState.GamePhase.LANDING_SEQUENCE
        }

        if (rocket.altitude <= 0f) {
            rocket.altitude = 0f
            state.gamePhase = GameState.GamePhase.LANDED_FAILED
        }
    }

    private fun landing(deltaTime: Float) {

        val rocket = state.rocket
        val planet = state.currentPlanet ?: return

        rocket.acceleration = -18f
        rocket.velocity += rocket.acceleration * deltaTime

        if (rocket.velocity < -60f) {
            rocket.velocity = -60f
        }

        rocket.altitude += rocket.velocity * deltaTime

        if (rocket.altitude <= 0f) {

            rocket.altitude = 0f

            if (rocket.velocity > -35f) {

                state.gamePhase = GameState.GamePhase.LANDED_SUCCESS
                state.totalCoins += planet.reward

            } else {

                state.gamePhase = GameState.GamePhase.LANDED_FAILED
            }
        }
    }

    private fun reset() {

        state.rocket.fuel = 100f
        state.rocket.altitude = 0f
        state.rocket.velocity = 0f
        state.rocket.acceleration = 0f
        state.rocket.stage = 0

        flightTime = 0f

        choosePlanet()

        state.gamePhase =
            GameState.GamePhase.AWAITING_FIRST_TAP
    }
}
