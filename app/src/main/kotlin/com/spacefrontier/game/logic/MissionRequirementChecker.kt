package com.spacefrontier.game.logic

import com.spacefrontier.game.models.Planet
import com.spacefrontier.game.models.Rocket

data class MissionRequirementResult(
    val canStart: Boolean,
    val message: String,
    val requiredAltitude: Float,
    val rocketAltitude: Float
)

object MissionRequirementChecker {

    fun check(
        rocket: Rocket,
        planet: Planet
    ): MissionRequirementResult {

        val requiredAltitude =
            planet.targetAltitude

        val rocketAltitude =
            rocket.maxAltitude

        if (rocketAltitude < requiredAltitude) {

            return MissionRequirementResult(
                canStart = false,
                message =
                    "🛑 RAKIETA ZA SŁABA\n" +
                            "Wymagana wysokość: " +
                            "${requiredAltitude.toInt()} km\n" +
                            "Twoja rakieta: " +
                            "${rocketAltitude.toInt()} km",
                requiredAltitude = requiredAltitude,
                rocketAltitude = rocketAltitude
            )
        }

        return MissionRequirementResult(
            canStart = true,
            message =
                "🚀 RAKIETA GOTOWA\n" +
                        "Wymagana wysokość: " +
                        "${requiredAltitude.toInt()} km\n" +
                        "Twoja rakieta: " +
                        "${rocketAltitude.toInt()} km",
            requiredAltitude = requiredAltitude,
            rocketAltitude = rocketAltitude
        )
    }
}
