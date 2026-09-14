package com.spacefrontier.game.models

data class PlanetProgress(
    val planetName: String,
    var unlocked: Boolean = false,
    var missionsCompleted: Int = 0
)

object PlanetProgressCatalog {

    val all = listOf(
        "Luna",
        "Mars",
        "Europa",
        "Titan",
        "Neptune"
    )
}
