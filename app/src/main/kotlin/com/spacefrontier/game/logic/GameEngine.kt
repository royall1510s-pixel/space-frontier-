package com.spacefrontier.game.logic

import android.content.Context
import com.spacefrontier.game.models.GameState
import com.spacefrontier.game.models.Planet
import com.spacefrontier.game.models.PlanetProgress
import com.spacefrontier.game.models.PlanetProgressCatalog
import com.spacefrontier.game.models.Planets
import com.spacefrontier.game.models.Rocket
import com.spacefrontier.game.models.RocketCatalog
import com.spacefrontier.game.models.RocketUpgrade

class GameEngine(
    context: Context
) {

    private val state = GameState()

    private val preferences =
        context.getSharedPreferences(
            "space_frontier_save",
            Context.MODE_PRIVATE
        )

    private var flightTime = 0f
    private var landingStartedAltitude = 0f

    private var selectedRocketId =
        preferences.getInt(
            "selected_rocket",
            1
        )

    private val unlockedRocketIds =
        mutableSetOf<Int>()

    private val rocketUpgrades =
        mutableMapOf<Int, RocketUpgrade>()

    private val planetProgress =
        mutableMapOf<String, PlanetProgress>()

    private var currentPlanetIndex =
        preferences.getInt(
            "current_planet_index",
            0
        )

    init {
        loadProgress()
        prepareRocket()
        choosePlanet()
    }

    private fun loadProgress() {

        state.totalCoins =
            preferences.getInt(
                "coins",
                0
            )

        val savedRockets =
            preferences.getStringSet(
                "unlocked_rockets",
                setOf("1")
            ) ?: setOf("1")

        unlockedRocketIds.clear()

        savedRockets.forEach {

            it.toIntOrNull()
                ?.let { id ->
                    unlockedRocketIds.add(id)
                }
        }

        unlockedRocketIds.add(1)

        if (
            !unlockedRocketIds.contains(
                selectedRocketId
            )
        ) {
            selectedRocketId = 1
        }

        loadRocketUpgrades()
        loadPlanetProgress()
    }

    private fun loadPlanetProgress() {

        planetProgress.clear()

        PlanetProgressCatalog.all.forEachIndexed {
                index,
                planetName ->

            val missionsCompleted =
                preferences.getInt(
                    "planet_${index}_missions",
                    0
                )

            val unlocked =
                preferences.getBoolean(
                    "planet_${index}_unlocked",
                    index == 0
                )

            planetProgress[planetName] =
                PlanetProgress(
                    planetName = planetName,
                    unlocked = unlocked,
                    missionsCompleted = missionsCompleted
                )
        }

        planetProgress[
            PlanetProgressCatalog.all.first()
        ]?.unlocked = true

        if (
            currentPlanetIndex !in
            PlanetProgressCatalog.all.indices
        ) {
            currentPlanetIndex = 0
        }

        while (
            currentPlanetIndex > 0 &&
            !isPlanetUnlocked(
                currentPlanetIndex
            )
        ) {
            currentPlanetIndex--
        }
    }

    private fun savePlanetProgress() {

        val editor =
            preferences.edit()

        PlanetProgressCatalog.all.forEachIndexed {
                index,
                planetName ->

            val progress =
                planetProgress[planetName]
                    ?: PlanetProgress(
                        planetName = planetName,
                        unlocked = index == 0,
                        missionsCompleted = 0
                    )

            editor.putBoolean(
                "planet_${index}_unlocked",
                progress.unlocked
            )

            editor.putInt(
                "planet_${index}_missions",
                progress.missionsCompleted
            )
        }

        editor.putInt(
            "current_planet_index",
            currentPlanetIndex
        )

        editor.apply()
    }

    private fun loadRocketUpgrades() {

        rocketUpgrades.clear()

        RocketCatalog.all.forEach { rocket ->

            val upgrade =
                RocketUpgrade(
                    rocketId = rocket.id,
                    engineLevel =
                        preferences.getInt(
                            "rocket_${rocket.id}_engine",
                            0
                        ),
                    fuelLevel =
                        preferences.getInt(
                            "rocket_${rocket.id}_fuel",
                            0
                        ),
                    altitudeLevel =
                        preferences.getInt(
                            "rocket_${rocket.id}_altitude",
                            0
                        )
                )

            rocketUpgrades[
                rocket.id
            ] = upgrade
        }
    }

    private fun saveProgress() {

        val editor =
            preferences.edit()

        editor.putInt(
            "coins",
            state.totalCoins
        )

        editor.putStringSet(
            "unlocked_rockets",
            unlockedRocketIds
                .map {
                    it.toString()
                }
                .toSet()
        )

        editor.putInt(
            "selected_rocket",
            selectedRocketId
        )

        rocketUpgrades.forEach { entry ->

            val rocketId =
                entry.key

            val upgrade =
                entry.value

            editor.putInt(
                "rocket_${rocketId}_engine",
                upgrade.engineLevel
            )

            editor.putInt(
                "rocket_${rocketId}_fuel",
                upgrade.fuelLevel
            )

            editor.putInt(
                "rocket_${rocketId}_altitude",
                upgrade.altitudeLevel
            )
        }

        editor.apply()

        savePlanetProgress()
    }

    fun getGameState(): GameState =
        state

    fun getRocketCatalog(): List<Rocket> =
        RocketCatalog.all.map { rocket ->

            val upgrade =
                getRocketUpgrade(
                    rocket.id
                )

            rocket.copy(
                thrust =
                    rocket.thrust +
                            upgrade.engineBonus,

                maxFuel =
                    rocket.maxFuel +
                            upgrade.fuelBonus,

                maxAltitude =
                    rocket.maxAltitude +
                            upgrade.altitudeBonus,

                fuel =
                    rocket.maxFuel +
                            upgrade.fuelBonus,

                unlocked =
                    unlockedRocketIds.contains(
                        rocket.id
                    )
            )
        }

    fun getSelectedRocket(): Rocket {

        val rocket =
            RocketCatalog.all.first {

                it.id ==
                        selectedRocketId
            }

        val upgrade =
            getRocketUpgrade(
                rocket.id
            )

        return rocket.copy(

            thrust =
                rocket.thrust +
                        upgrade.engineBonus,

            maxFuel =
                rocket.maxFuel +
                        upgrade.fuelBonus,

            maxAltitude =
                rocket.maxAltitude +
                        upgrade.altitudeBonus,

            fuel =
                rocket.maxFuel +
                        upgrade.fuelBonus,

            unlocked = true
        )
    }

    fun getRocketUpgrade(
        rocketId: Int
    ): RocketUpgrade {

        return rocketUpgrades.getOrPut(
            rocketId
        ) {
            RocketUpgrade(
                rocketId = rocketId
            )
        }
    }

    fun upgradeEngine(
        rocketId: Int
    ): Boolean {

        if (
            !unlockedRocketIds.contains(
                rocketId
            )
        ) {
            return false
        }

        val upgrade =
            getRocketUpgrade(
                rocketId
            )

        val cost =
            upgrade.engineCost

        if (
            state.totalCoins <
            cost
        ) {
            return false
        }

        state.totalCoins -= cost

        upgrade.engineLevel++

        if (
            rocketId ==
            selectedRocketId
        ) {
            prepareRocket()
        }

        saveProgress()

        return true
    }

    fun upgradeFuel(
        rocketId: Int
    ): Boolean {

        if (
            !unlockedRocketIds.contains(
                rocketId
            )
        ) {
            return false
        }

        val upgrade =
            getRocketUpgrade(
                rocketId
            )

        val cost =
            upgrade.fuelCost

        if (
            state.totalCoins <
            cost
        ) {
            return false
        }

        state.totalCoins -= cost

        upgrade.fuelLevel++

        if (
            rocketId ==
            selectedRocketId
        ) {
            prepareRocket()
        }

        saveProgress()

        return true
    }

    fun upgradeAltitude(
        rocketId: Int
    ): Boolean {

        if (
            !unlockedRocketIds.contains(
                rocketId
            )
        ) {
            return false
        }

        val upgrade =
            getRocketUpgrade(
                rocketId
            )

        val 
