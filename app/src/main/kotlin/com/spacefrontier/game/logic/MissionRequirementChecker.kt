package com.spacefrontier.game.logic

import com.spacefrontier.game.models.PlanetMission
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
        mission: PlanetMission
    ): MissionRequirementResult {

        val requiredAltitude =
            mission.distance.toFloat()

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
