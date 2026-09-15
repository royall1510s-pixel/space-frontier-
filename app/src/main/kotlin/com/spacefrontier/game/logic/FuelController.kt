package com.spacefrontier.game.logic

import com.spacefrontier.game.models.PlanetMission
import com.spacefrontier.game.models.Rocket

data class FuelUpdateResult(
    val fuel: Float,
    val maxFuel: Float,
    val status: FuelStatus,
    val fuelConsumed: Float,
    val shouldEmergencyLand: Boolean
)

object FuelController {

    fun updateLaunchFuel(
        rocket: Rocket,
        mission: PlanetMission,
        stage: Int,
        deltaTime: Float
    ): FuelUpdateResult {

        val dt =
            deltaTime.coerceIn(
                0f,
                0.05f
            )

        val rate =
            FlightBalance.launchFuelRate(
                stage = stage,
                mission = mission
            )

        return consumeFuel(
            rocket = rocket,
            consumption =
                rate * dt
        )
    }

    fun updateFlightFuel(
        rocket: Rocket,
        mission: PlanetMission,
        deltaTime: Float
    ): FuelUpdateResult {

        val dt =
            deltaTime.coerceIn(
                0f,
                0.05f
            )

        val rate =
            FlightBalance.flightFuelRate(
                mission
            )

        return consumeFuel(
            rocket = rocket,
            consumption =
                rate * dt
        )
    }

    fun consumeFuel(
        rocket: Rocket,
        consumption: Float
    ): FuelUpdateResult {

        val safeConsumption =
            consumption.coerceAtLeast(
                0f
            )

        val oldFuel =
            rocket.fuel.coerceAtLeast(
                0f
            )

        val newFuel =
            FlightBalance.clampFuel(
                fuel =
                    oldFuel -
                        safeConsumption,
                maxFuel =
                    rocket.maxFuel
            )

        val consumed =
            oldFuel - newFuel

        val status =
            FuelStatus.from(
                fuel = newFuel,
                maxFuel = rocket.maxFuel
            )

        return FuelUpdateResult(
            fuel = newFuel,
            maxFuel = rocket.maxFuel,
            status = status,
            fuelConsumed = consumed,
            shouldEmergencyLand =
                status.isEmpty
        )
    }

    fun getStatus(
        rocket: Rocket
    ): FuelStatus {

        return FuelStatus.from(
            fuel = rocket.fuel,
            maxFuel = rocket.maxFuel
        )
    }

    fun getWarning(
        rocket: Rocket
    ): String? {

        val status =
            getStatus(
                rocket
            )

        return when {

            status.isEmpty ->
                "🚨 BRAK PALIWA"

            status.isCritical ->
                "🔴 KRYTYCZNE PALIWO"

            status.isLow ->
                "🟡 NISKI POZIOM PALIWA"

            else ->
                null
        }
    }

    fun canContinueFlight(
        rocket: Rocket
    ): Boolean {

        return !getStatus(
            rocket
        ).isEmpty
    }

    fun fuelRatio(
        rocket: Rocket
    ): Float {

        return getStatus(
            rocket
        ).percentage
    }

    fun remainingFuel(
        rocket: Rocket
    ): Float {

        return rocket.fuel
            .coerceAtLeast(
                0f
            )
    }

    fun maxFuel(
        rocket: Rocket
    ): Float {

        return rocket.maxFuel
            .coerceAtLeast(
                0f
            )
    }
}
