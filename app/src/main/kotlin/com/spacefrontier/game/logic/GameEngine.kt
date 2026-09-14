package com.spacefrontier.game.logic

import android.content.Context
import com.spacefrontier.game.models.GameState
import com.spacefrontier.game.models.Planets
import com.spacefrontier.game.models.Rocket
import com.spacefrontier.game.models.RocketCatalog

class GameEngine(
    context: Context
) {

    private val state = GameState()

    private val preferences =
        context.getSharedPreferences(
            "space_frontier_save",
            Context.MODE_PRIVATE
        )

    private var flightTime = 0f
    private var landingStartedAltitude = 0f

    private var selectedRocketId =
        preferences.getInt(
            "selected_rocket",
            1
        )

    private val unlockedRocketIds =
        mutableSetOf<Int>()

    init {

        loadProgress()

        prepareRocket()

        choosePlanet()
    }

    private fun loadProgress() {

        state.totalCoins =
            preferences.getInt(
                "coins",
                0
            )

        val savedRockets =
            preferences.getStringSet(
                "unlocked_rockets",
                setOf("1")
            )
                ?: setOf("1")

        unlockedRocketIds.clear()

        savedRockets.forEach {

            it.toIntOrNull()
                ?.let { id ->
                    unlockedRocketIds.add(id)
                }
        }

        unlockedRocketIds.add(1)

        if (
            !unlockedRocketIds.contains(
                selectedRocketId
            )
        ) {

            selectedRocketId = 1
        }
    }

    private fun saveProgress() {

        preferences.edit()
            .putInt(
                "coins",
                state.totalCoins
            )
            .putStringSet(
                "unlocked_rockets",
                unlockedRocketIds
                    .map { it.toString() }
                    .toSet()
            )
            .putInt(
                "selected_rocket",
                selectedRocketId
            )
            .apply()
    }

    fun getGameState(): GameState =
        state

    fun getRocketCatalog(): List<Rocket> =
        RocketCatalog.all.map { rocket ->

            rocket.copy(
                unlocked =
                    unlockedRocketIds.contains(
                        rocket.id
                    )
            )
        }

    fun getSelectedRocket(): Rocket =
        RocketCatalog.all.first {

            it.id ==
                    selectedRocketId
        }

    fun isRocketUnlocked(
        rocketId: Int
    ): Boolean =
        unlockedRocketIds.contains(
            rocketId
        )

    fun buyRocket(
        rocketId: Int
    ): Boolean {

        val rocket =
            RocketCatalog.all.firstOrNull {

                it.id ==
                        rocketId
            }
                ?: return false

        if (
            unlockedRocketIds.contains(
                rocketId
            )
        ) {

            return false
        }

        if (
            state.totalCoins <
            rocket.price
        ) {

            return false
        }

        state.totalCoins -=
            rocket.price

        unlockedRocketIds.add(
            rocketId
        )

        selectedRocketId =
            rocketId

        prepareRocket()

        saveProgress()

        return true
    }

    fun selectRocket(
        rocketId: Int
    ): Boolean {

        if (
            !unlockedRocketIds.contains(
                rocketId
            )
        ) {

            return false
        }

        if (
            state.gamePhase !=
            GameState.GamePhase.AWAITING_FIRST_TAP &&
            state.gamePhase !=
            GameState.GamePhase.LANDED_SUCCESS &&
            state.gamePhase !=
            GameState.GamePhase.LANDED_FAILED
        ) {

            return false
        }

        selectedRocketId =
            rocketId

        prepareRocket()

        saveProgress()

        return true
    }

    private fun prepareRocket() {

        val template =
            getSelectedRocket()

        state.rocket =
            template.copy(
                fuel = template.maxFuel,
                altitude = 0f,
                velocity = 0f,
                acceleration = 0f,
                stage = 0,
                unlocked = true
            )
    }

    private fun choosePlanet() {

        state.currentPlanet =
            Planets.all.random()
    }

    fun handleTap() {

        when (
            state.gamePhase
        ) {

            GameState.GamePhase.AWAITING_FIRST_TAP -> {

                state.gamePhase =
                    GameState.GamePhase.STAGE_1_ACTIVE

                state.rocket.stage =
                    1
            }

            GameState.GamePhase.STAGE_1_ACTIVE -> {

                state.gamePhase =
                    GameState.GamePhase.STAGE_2_ACTIVE

                state.rocket.stage =
                    2
            }

            GameState.GamePhase.STAGE_2_ACTIVE -> {

                state.gamePhase =
                    GameState.GamePhase.STAGE_3_ACTIVE

                state.rocket.stage =
                    3
            }

            GameState.GamePhase.LANDED_SUCCESS,
            GameState.GamePhase.LANDED_FAILED -> {

                reset()
            }

            else -> Unit
        }
    }

    fun update(
        deltaTime: Float
    ) {

        val dt =
            deltaTime.coerceIn(
                0f,
                0.05f
            )

        when (
            state.gamePhase
        ) {

            GameState.GamePhase.STAGE_1_ACTIVE ->
                launch(
                    dt,
                    1
                )

            GameState.GamePhase.STAGE_2_ACTIVE ->
                launch(
                    dt,
                    2
                )

            GameState.GamePhase.STAGE_3_ACTIVE -> {

                launch(
                    dt,
                    3
                )

                if (
                    state.rocket.altitude >=
                    40f
                ) {

                    state.gamePhase =
                        GameState.GamePhase.IN_FLIGHT
                }
            }

            GameState.GamePhase.IN_FLIGHT ->
                fly(dt)

            GameState.GamePhase.LANDING_SEQUENCE ->
                landing(dt)

            else -> Unit
        }
    }

    private fun launch(
        deltaTime: Float,
        stage: Int
    ) {

        val rocket =
            state.rocket

        val baseThrust =
            rocket.thrust

        val thrust =
            when (stage) {

                1 ->
                    baseThrust

                2 ->
                    baseThrust * 1.45f

                else ->
                    baseThrust * 2.0f
            }

        val fuelConsumption =
            when (stage) {

                1 ->
                    4f

                2 ->
                    6f

                else ->
                    8f
            }

        rocket.acceleration =
            thrust

        rocket.velocity +=
            rocket.acceleration *
                    deltaTime

        rocket.altitude +=
            rocket.velocity *
                    deltaTime

        rocket.fuel -=
            fuelConsumption *
                    deltaTime

        rocket.fuel =
            rocket.fuel.coerceAtLeast(
                0f
            )

        if (
            rocket.fuel <= 0f
        ) {

            startLanding()
        }

        if (
            rocket.altitude >=
            rocket.maxAltitude
        ) {

            startLanding()
        }
    }

    private fun fly(
        deltaTime: Float
    ) {

        val rocket =
            state.rocket

        val planet =
            state.currentPlanet
                ?: return

        flightTime +=
            deltaTime

        rocket.acceleration =
            -6f

        rocket.velocity +=
            rocket.acceleration *
                    deltaTime

        if (
            rocket.velocity < 20f
        ) {

            rocket.velocity =
                20f
        }

        rocket.altitude +=
            rocket.velocity *
                    deltaTime

        rocket.fuel -=
            2f *
                    deltaTime

        rocket.fuel =
            rocket.fuel.coerceAtLeast(
                0f
            )

        if (
            rocket.altitude >=
            planet.targetAltitude ||
            rocket.fuel <= 0f ||
            flightTime >= 12f
        ) {

            startLanding()
        }
    }

    private fun startLanding() {

        if (
            state.gamePhase !=
            GameState.GamePhase.LANDING_SEQUENCE
        ) {

            landingStartedAltitude =
                state.rocket.altitude

            state.gamePhase =
                GameState.GamePhase.LANDING_SEQUENCE

            state.rocket.velocity =
                state.rocket.velocity
                    .coerceAtLeast(
                        25f
                    )
        }
    }

    private fun landing(
        deltaTime: Float
    ) {

        val rocket =
            state.rocket

        val planet =
            state.currentPlanet
                ?: return

        val distanceToGround =
            landingStartedAltitude
                .coerceAtLeast(
                    1f
                )

        val brakingForce =
            12f +
                    (
                        distanceToGround /
                                100f
                    ).coerceAtMost(
                        10f
                    )

        rocket.acceleration =
            -brakingForce

        rocket.velocity +=
            rocket.acceleration *
                    deltaTime

        if (
            rocket.velocity < -45f
        ) {

            rocket.velocity =
                -45f
        }

        rocket.altitude +=
            rocket.velocity *
                    deltaTime

        if (
            rocket.altitude <= 0f
        ) {

            rocket.altitude =
                0f

            if (
                rocket.velocity >= -35f
            ) {

                rocket.velocity =
                    0f

                rocket.acceleration =
                    0f

                state.gamePhase =
                    GameState.GamePhase.LANDED_SUCCESS

                state.totalCoins +=
                    planet.reward

                saveProgress()

            } else {

                rocket.velocity =
                    0f

                rocket.acceleration =
                    0f

                state.gamePhase =
                    GameState.GamePhase.LANDED_FAILED
            }
        }
    }

    private fun reset() {

        prepareRocket()

        flightTime =
            0f

        landingStartedAltitude =
            0f

        choosePlanet()

        state.gamePhase =
            GameState.GamePhase.AWAITING_FIRST_TAP
    }
}
