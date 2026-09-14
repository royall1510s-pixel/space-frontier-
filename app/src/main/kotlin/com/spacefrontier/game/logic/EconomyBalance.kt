package com.spacefrontier.game.logic

object EconomyBalance {

    const val MAX_UPGRADE_LEVEL = 10

    private const val ENGINE_UPGRADE_BASE = 150
    private const val FUEL_UPGRADE_BASE = 125
    private const val ALTITUDE_UPGRADE_BASE = 175

    fun upgradeCost(
        baseCost: Int,
        level: Int
    ): Int {

        if (level >= MAX_UPGRADE_LEVEL) {
            return Int.MAX_VALUE
        }

        val nextLevel =
            level + 1

        return baseCost *
                nextLevel *
                nextLevel
    }

    fun engineUpgradeCost(
        level: Int
    ): Int =
        upgradeCost(
            ENGINE_UPGRADE_BASE,
            level
        )

    fun fuelUpgradeCost(
        level: Int
    ): Int =
        upgradeCost(
            FUEL_UPGRADE_BASE,
            level
        )

    fun altitudeUpgradeCost(
        level: Int
    ): Int =
        upgradeCost(
            ALTITUDE_UPGRADE_BASE,
            level
        )

    fun missionReward(
        planetReward: Int,
        rocketId: Int,
        engineLevel: Int,
        fuelLevel: Int,
        altitudeLevel: Int,
        altitude: Float,
        missionRewardMultiplier: Float
    ): Int {

        val rocketBonus =
            when {
                rocketId >= 20 -> 1000
                rocketId >= 18 -> 700
                rocketId >= 15 -> 500
                rocketId >= 12 -> 300
                rocketId >= 9 -> 200
                rocketId >= 6 -> 100
                else -> 0
            }

        val safeEngineLevel =
            engineLevel.coerceIn(
                0,
                MAX_UPGRADE_LEVEL
            )

        val safeFuelLevel =
            fuelLevel.coerceIn(
                0,
                MAX_UPGRADE_LEVEL
            )

        val safeAltitudeLevel =
            altitudeLevel.coerceIn(
                0,
                MAX_UPGRADE_LEVEL
            )

        val upgradeBonus =
            (
                safeEngineLevel +
                        safeFuelLevel +
                        safeAltitudeLevel
                ) * 25

        val altitudeBonus =
            (
                altitude.coerceAtLeast(0f) /
                        100f
                ).toInt() * 10

        val rawReward =
            (
                planetReward +
                        rocketBonus +
                        upgradeBonus +
                        altitudeBonus
                ) *
                    missionRewardMultiplier

        return rawReward
            .toInt()
            .coerceAtLeast(
                planetReward
            )
    }
}
