package com.spacefrontier.game.logic

import com.spacefrontier.game.models.PlanetMission
import com.spacefrontier.game.models.Rocket

/**
 * Obliczenia lotu rakiety.
 *
 * Klasa korzysta z centralnego FlightBalance,
 * dzięki czemu parametry lotu są w jednym miejscu.
 */
object FlightCalculator {

    data class FlightStep(
        val altitude: Float,
        val velocity: Float,
        val acceleration: Float,
        val fuel: Float,
        val fuelStatus: FuelStatus
    )

    fun calculateLaunchStep(
        rocket: Rocket,
        mission: PlanetMission,
        stage: Int,
        deltaTime: Float
    ): FlightStep {

        val dt =
            deltaTime.coerceIn(
                0f,
                0.05f
            )

        val thrustMultiplier =
            FlightBalance.thrustMultiplier(
                stage
            )

        val acceleration =
            rocket.thrust *
                    thrustMultiplier

        val newVelocity =
            rocket.velocity +
                    acceleration *
                    dt

        val newAltitude =
            (
                rocket.altitude +
                        newVelocity *
                        dt
                ).coerceAtLeast(0f)

        val fuelRate =
            FlightBalance.launchFuelRate(
                stage = stage,
                mission = mission
            )

        val newFuel =
            FlightBalance.clampFuel(
                fuel =
                    rocket.fuel -
                            fuelRate *
                            dt,
                maxFuel =
                    rocket.maxFuel
            )

        return FlightStep(
            altitude = newAltitude,
            velocity = newVelocity,
            acceleration = acceleration,
            fuel = newFuel,
            fuelStatus =
                FuelStatus.from(
                    fuel = newFuel,
                    maxFuel = rocket.maxFuel
                )
        )
    }

    fun calculateFlightStep(
        rocket: Rocket,
        mission: PlanetMission,
        deltaTime: Float
    ): FlightStep {

        val dt =
            deltaTime.coerceIn(
                0f,
                0.05f
            )

        val acceleration =
            rocket.thrust

        val newVelocity =
            (
                rocket.velocity +
                        acceleration *
                        dt
                ).coerceAtLeast(
                    FlightBalance.MIN_FLIGHT_VELOCITY
                )

        val newAltitude =
            (
                rocket.altitude +
                        newVelocity *
                        dt
                ).coerceAtLeast(0f)

        val fuelRate =
            FlightBalance.flightFuelRate(
                mission
            )

        val newFuel =
            FlightBalance.clampFuel(
                fuel =
                    rocket.fuel -
                            fuelRate *
                            dt,
                maxFuel =
                    rocket.maxFuel
            )

        return FlightStep(
            altitude = newAltitude,
            velocity = newVelocity,
            acceleration = acceleration,
            fuel = newFuel,
            fuelStatus =
                FuelStatus.from(
                    fuel = newFuel,
                    maxFuel = rocket.maxFuel
                )
        )
    }

    fun shouldStartLanding(
        rocket: Rocket,
        mission: PlanetMission,
        flightTime: Float
    ): Boolean {

        return FlightBalance.shouldEndFlight(
            altitude =
                rocket.altitude,
            targetAltitude =
                missionTargetAltitude(
                    rocket = rocket,
                    mission = mission
                ),
            fuel =
                rocket.fuel,
            flightTime =
                flightTime
        )
    }

    fun isFuelCritical(
        rocket: Rocket
    ): Boolean {

        return FlightBalance.isFuelCritical(
            fuel =
                rocket.fuel,
            maxFuel =
                rocket.maxFuel
        )
    }

    fun isFuelEmpty(
        rocket: Rocket
    ): Boolean {

        return FlightBalance.isFuelEmpty(
            rocket.fuel
        )
    }

    fun getFuelStatus(
        rocket: Rocket
    ): FuelStatus {

        return FuelStatus.from(
            fuel =
                rocket.fuel,
            maxFuel =
                rocket.maxFuel
        )
    }

    private fun missionTargetAltitude(
        rocket: Rocket,
        mission: PlanetMission
    ): Float {

        return mission.distance
            .toFloat()
            .coerceAtLeast(
                rocket.altitude
            )
    }
}
