package com.spacefrontier.game.models

import com.spacefrontier.game.logic.EconomyBalance

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
        get() = engineLevel.coerceIn(0, MAX_LEVEL) * 8f

    val fuelBonus: Float
        get() = fuelLevel.coerceIn(0, MAX_LEVEL) * 15f

    val altitudeBonus: Float
        get() = altitudeLevel.coerceIn(0, MAX_LEVEL) * 35f

    val engineCost: Int
        get() = EconomyBalance.engineUpgradeCost(
            engineLevel
        )

    val fuelCost: Int
        get() = EconomyBalance.fuelUpgradeCost(
            fuelLevel
        )

    val altitudeCost: Int
        get() = EconomyBalance.altitudeUpgradeCost(
            altitudeLevel
        )

    val engineMaxed: Boolean
        get() = engineLevel >= MAX_LEVEL

    val fuelMaxed: Boolean
        get() = fuelLevel >= MAX_LEVEL

    val altitudeMaxed: Boolean
        get() = altitudeLevel >= MAX_LEVEL
}
