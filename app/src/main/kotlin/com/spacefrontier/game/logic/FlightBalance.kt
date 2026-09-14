package com.spacefrontier.game.logic

import com.spacefrontier.game.models.PlanetMission

/**
 * Centralny balans lotu rakiety.
 *
 * Wszystkie podstawowe wartości związane z paliwem,
 * prędkością i zużyciem paliwa są trzymane w jednym miejscu.
 */
object FlightBalance {

    const val STAGE_1_THRUST_MULTIPLIER = 1.0f
    const val STAGE_2_THRUST_MULTIPLIER = 1.45f
    const val STAGE_3_THRUST_MULTIPLIER = 2.0f

    const val STAGE_1_FUEL_RATE = 4.0f
    const val STAGE_2_FUEL_RATE = 6.0f
    const val STAGE_3_FUEL_RATE = 8.0f

    const val FLIGHT_FUEL_RATE = 2.0f

    const val MIN_FLIGHT_VELOCITY = 20.0f

    const val MIN_LANDING_VELOCITY = 25.0f
    const val SAFE_LANDING_VELOCITY = 35.0f
    const val MAX_LANDING_VELOCITY = 45.0f

    const val FLIGHT_TIME_LIMIT = 12.0f

    const val FLIGHT_START_ALTITUDE = 40.0f

    fun thrustMultiplier(
        stage: Int
    ): Float {

        return when (stage) {

            1 ->
                STAGE_1_THRUST_MULTIPLIER

            2 ->
                STAGE_2_THRUST_MULTIPLIER

            else ->
                STAGE_3_THRUST_MULTIPLIER
        }
    }

    fun launchFuelRate(
        stage: Int,
        mission: PlanetMission
    ): Float {

        val baseRate =
            when (stage) {

                1 ->
                    STAGE_1_FUEL_RATE

                2 ->
                    STAGE_2_FUEL_RATE

                else ->
                    STAGE_3_FUEL_RATE
            }

        return baseRate *
                mission.fuelMultiplier
    }

    fun flightFuelRate(
        mission: PlanetMission
    ): Float {

        return FLIGHT_FUEL_RATE *
                mission.fuelMultiplier
    }

    fun isFuelCritical(
        fuel: Float,
        maxFuel: Float
    ): Boolean {

        if (maxFuel <= 0f) {
            return true
        }

        return fuel <=
                maxFuel * 0.20f
    }

    fun isFuelEmpty(
        fuel: Float
    ): Boolean {

        return fuel <= 0f
    }

    fun clampFuel(
        fuel: Float,
        maxFuel: Float
    ): Float {

        return fuel.coerceIn(
            0f,
            maxFuel.coerceAtLeast(0f)
        )
    }

    fun landingVelocityLimit(
        landingStartedAltitude: Float
    ): Float {

        val altitudeBonus =
            (
                landingStartedAltitude /
                        100f
                ).coerceAtMost(10f)

        return (
            SAFE_LANDING_VELOCITY +
                    altitudeBonus
            ).coerceAtMost(
                MAX_LANDING_VELOCITY
            )
    }

    fun landingAcceleration(
        landingStartedAltitude: Float
    ): Float {

        val altitudeBonus =
            (
                landingStartedAltitude /
                        100f
                ).coerceAtMost(10f)

        return -(
            12f +
                    altitudeBonus
            )
    }

    fun shouldEndFlight(
        altitude: Float,
        targetAltitude: Float,
        fuel: Float,
        flightTime: Float
    ): Boolean {

        return altitude >= targetAltitude ||
                fuel <= 0f ||
                flightTime >= FLIGHT_TIME_LIMIT
    }
}
