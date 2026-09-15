package com.spacefrontier.game.logic

import com.spacefrontier.game.models.PlanetMission
import com.spacefrontier.game.models.Rocket

/**
 * Kontroler pojedynczego kroku lotu.
 *
 * Łączy rakietę, misję oraz centralny kalkulator lotu.
 */
object FlightController {

    fun launch(
        rocket: Rocket,
        mission: PlanetMission,
        stage: Int,
        deltaTime: Float
    ): FlightCalculator.FlightStep {

        return FlightCalculator.calculateLaunchStep(
            rocket = rocket,
            mission = mission,
            stage = stage,
            deltaTime = deltaTime
        )
    }

    fun fly(
        rocket: Rocket,
        mission: PlanetMission,
        deltaTime: Float
    ): FlightCalculator.FlightStep {

        return FlightCalculator.calculateFlightStep(
            rocket = rocket,
            mission = mission,
            deltaTime = deltaTime
        )
    }

    fun fuelStatus(
        rocket: Rocket
    ): FuelStatus {

        return FuelStatus.from(
            fuel = rocket.fuel,
            maxFuel = rocket.maxFuel
        )
    }

    fun fuelCritical(
        rocket: Rocket
    ): Boolean {

        return FlightBalance.isFuelCritical(
            fuel = rocket.fuel,
            maxFuel = rocket.maxFuel
        )
    }

    fun fuelEmpty(
        rocket: Rocket
    ): Boolean {

        return FlightBalance.isFuelEmpty(
            rocket.fuel
        )
    }

    fun landingVelocityLimit(
        altitude: Float
    ): Float {

        return FlightBalance.landingVelocityLimit(
            landingStartedAltitude = altitude
        )
    }

    fun landingAcceleration(
        altitude: Float
    ): Float {

        return FlightBalance.landingAcceleration(
            landingStartedAltitude = altitude
        )
    }
}
