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

        RocketCatalog.all.forEach { 
