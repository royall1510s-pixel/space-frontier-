package com.spacefrontier.game.models

data class RocketUpgrade(
    val rocketId: Int,
    var engineLevel: Int = 0,
    var fuelLevel: Int = 0,
    var altitudeLevel: Int = 0
) {

    companion object {
        const val MAX_LEVEL = 10
    }

    val engineBonus: Float
        get() = engineLevel.coerceAtMost(MAX_LEVEL) * 8f

    val fuelBonus: Float
        get() = fuelLevel.coerceAtMost(MAX_LEVEL) * 15f

    val altitudeBonus: Float
        get() = altitudeLevel.coerceAtMost(MAX_LEVEL) * 35f

    val engineCost: Int
        get() = upgradeCost(
            baseCost = 150,
            level = engineLevel
        )

    val fuelCost: Int
        get() = upgradeCost(
            baseCost = 125,
            level = fuelLevel
        )

    val altitudeCost: Int
        get() = upgradeCost(
            baseCost = 175,
            level = altitudeLevel
        )

    val engineMaxed: Boolean
        get() = engineLevel >= MAX_LEVEL

    val fuelMaxed: Boolean
        get() = fuelLevel >= MAX_LEVEL

    val altitudeMaxed: Boolean
        get() = altitudeLevel >= MAX_LEVEL

    private fun upgradeCost(
        baseCost: Int,
        level: Int
    ): Int {

        if (level >= MAX_LEVEL) {
            return Int.MAX_VALUE
        }

        return baseCost *
                (level + 1) *
                (level + 1)
    }
}
