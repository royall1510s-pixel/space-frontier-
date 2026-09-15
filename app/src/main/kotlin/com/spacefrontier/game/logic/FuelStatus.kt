package com.spacefrontier.game.logic

data class FuelStatus(
    val fuel: Float,
    val maxFuel: Float
) {

    val percentage: Float
        get() {
            if (maxFuel <= 0f) {
                return 0f
            }

            return (
                fuel / maxFuel
            ).coerceIn(
                0f,
                1f
            )
        }

    val percentageInt: Int
        get() =
            (
                percentage * 100f
            ).toInt()

    val isEmpty: Boolean
        get() =
            fuel <= 0f

    val isCritical: Boolean
        get() =
            percentage <= 0.20f

    val isLow: Boolean
        get() =
            percentage <= 0.40f

    val isGood: Boolean
        get() =
            percentage > 0.40f

    val displayText: String
        get() =
            "${fuel.toInt()} / ${maxFuel.toInt()}"

    val percentageText: String
        get() =
            "$percentageInt%"

    companion object {

        fun from(
            fuel: Float,
            maxFuel: Float
        ): FuelStatus {

            return FuelStatus(
                fuel = fuel.coerceAtLeast(0f),
                maxFuel = maxFuel.coerceAtLeast(0f)
            )
        }
    }
}
