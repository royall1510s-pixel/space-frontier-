package com.spacefrontier.game.models

data class RocketUpgrade(
    val rocketId: Int,
    var engineLevel: Int = 0,
    var fuelLevel: Int = 0,
    var altitudeLevel: Int = 0
) {

    val engineBonus: Float
        get() = engineLevel * 8f

    val fuelBonus: Float
        get() = fuelLevel * 15f

    val altitudeBonus: Float
        get() = altitudeLevel * 35f

    val engineCost: Int
        get() = 150 * (engineLevel + 1)

    val fuelCost: Int
        get() = 125 * (fuelLevel + 1)

    val altitudeCost: Int
        get() = 175 * (altitudeLevel + 1)
}
