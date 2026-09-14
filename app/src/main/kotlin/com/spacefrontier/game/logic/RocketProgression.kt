package com.spacefrontier.game.logic

import com.spacefrontier.game.models.RocketCatalog

/**
 * Controls the order in which rockets can be purchased.
 *
 * A rocket can be purchased only when the previous rocket in the
 * catalog has already been unlocked.
 */
object RocketProgression {

    fun getPreviousRocketId(
        rocketId: Int
    ): Int? {

        val index =
            RocketCatalog.all.indexOfFirst {
                it.id == rocketId
            }

        if (index <= 0) {
            return null
        }

        return RocketCatalog.all[index - 1].id
    }

    fun canPurchase(
        rocketId: Int,
        unlockedRocketIds: Set<Int>
    ): Boolean {

        if (rocketId == 1) {
            return false
        }

        val rocketExists =
            RocketCatalog.all.any {
                it.id == rocketId
            }

        if (!rocketExists) {
            return false
        }

        val previousRocketId =
            getPreviousRocketId(
                rocketId
            ) ?: return false

        return unlockedRocketIds.contains(
            previousRocketId
        )
    }

    fun getRequiredPreviousRocketName(
        rocketId: Int
    ): String? {

        val previousRocketId =
            getPreviousRocketId(
                rocketId
            ) ?: return null

        return RocketCatalog.all
            .firstOrNull {
                it.id == previousRocketId
            }
            ?.name
    }
}
