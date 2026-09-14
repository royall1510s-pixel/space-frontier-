package com.spacefrontier.game.models

data class PlanetMission(
    val planetName: String,
    val distance: Int,
    val difficulty: Int,
    val fuelMultiplier: Float,
    val rewardMultiplier: Float,
    val description: String
)

object PlanetMissionCatalog {

    val all = listOf(

        PlanetMission(
            planetName = "Luna",
            distance = 120,
            difficulty = 1,
            fuelMultiplier = 1.0f,
            rewardMultiplier = 1.0f,
            description =
                "Pierwsza wyprawa. Idealna do rozpoczęcia eksploracji."
        ),

        PlanetMission(
            planetName = "Mars",
            distance = 225,
            difficulty = 2,
            fuelMultiplier = 1.20f,
            rewardMultiplier = 1.50f,
            description =
                "Czerwona planeta. Dłuższy lot i większe zużycie paliwa."
        ),

        PlanetMission(
            planetName = "Europa",
            distance = 360,
            difficulty = 3,
            fuelMultiplier = 1.45f,
            rewardMultiplier = 2.25f,
            description =
                "Lodowy księżyc Jowisza. Misja wymaga lepszej rakiety."
        ),

        PlanetMission(
            planetName = "Titan",
            distance = 520,
            difficulty = 4,
            fuelMultiplier = 1.75f,
            rewardMultiplier = 3.25f,
            description =
                "Odległy świat Saturna. Bardzo wymagająca wyprawa."
        ),

        PlanetMission(
            planetName = "Neptune",
            distance = 850,
            difficulty = 5,
            fuelMultiplier = 2.20f,
            rewardMultiplier = 5.00f,
            description =
                "Najdalszy cel. Ostateczny sprawdzian możliwości rakiety."
        )
    )

    fun getForPlanet(
        planetName: String
    ): PlanetMission {

        return all.firstOrNull {
            it.planetName == planetName
        } ?: all.first()
    }
}
