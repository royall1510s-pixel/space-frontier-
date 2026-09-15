package com.spacefrontier.game.logic

import android.content.Context
import com.spacefrontier.game.models.GameState
import com.spacefrontier.game.models.Planet
import com.spacefrontier.game.models.PlanetMissionCatalog
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
            it.toIntOrNull()?.let { id ->
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
                it.id == selectedRocketId
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

        if (
            upgrade.engineMaxed
        ) {
            return false
        }

        val cost =
            upgrade.engineCost

        if (
            state.totalCoins < cost
        ) {
            return false
        }

        state.totalCoins -= cost
        upgrade.engineLevel++

        if (
            rocketId == selectedRocketId
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

        if (
            upgrade.fuelMaxed
        ) {
            return false
        }

        val cost =
            upgrade.fuelCost

        if (
            state.totalCoins < cost
        ) {
            return false
        }

        state.totalCoins -= cost
        upgrade.fuelLevel++

        if (
            rocketId == selectedRocketId
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

        if (
            upgrade.altitudeMaxed
        ) {
            return false
        }

        val cost =
            upgrade.altitudeCost

        if (
            state.totalCoins < cost
        ) {
            return false
        }

        state.totalCoins -= cost
        upgrade.altitudeLevel++

        if (
            rocketId == selectedRocketId
        ) {
            prepareRocket()
        }

        saveProgress()

        return true
    }

    fun buyRocket(
        rocketId: Int
    ): Boolean {

        val rocket =
            RocketCatalog.all.firstOrNull {
                it.id == rocketId
            } ?: return false

        if (
            unlockedRocketIds.contains(
                rocketId
            )
        ) {
            return false
        }

        if (
            !RocketProgression.canPurchase(
                rocketId = rocketId,
                unlockedRocketIds =
                    unlockedRocketIds
            )
        ) {
            return false
        }

        if (
            state.totalCoins < rocket.price
        ) {
            return false
        }

        state.totalCoins -=
            rocket.price

        unlockedRocketIds.add(
            rocketId
        )

        selectedRocketId =
            rocketId

        prepareRocket()
        saveProgress()

        return true
    }

    fun selectRocket(
        rocketId: Int
    ): Boolean {

        if (
            !unlockedRocketIds.contains(
                rocketId
            )
        ) {
            return false
        }

        if (
            state.gamePhase !=
            GameState.GamePhase.AWAITING_FIRST_TAP &&
            state.gamePhase !=
            GameState.GamePhase.LANDED_SUCCESS &&
            state.gamePhase !=
            GameState.GamePhase.LANDED_FAILED
        ) {
            return false
        }

        selectedRocketId =
            rocketId

        prepareRocket()
        saveProgress()

        return true
    }

    fun getPlanetProgress():
            List<PlanetProgress> {

        return PlanetProgressCatalog.all.map {
                planetName ->

            planetProgress[planetName]
                ?: PlanetProgress(
                    planetName = planetName,
                    unlocked = false,
                    missionsCompleted = 0
                )
        }
    }

    fun isPlanetUnlocked(
        planetIndex: Int
    ): Boolean {

        if (
            planetIndex !in
            PlanetProgressCatalog.all.indices
        ) {
            return false
        }

        val planetName =
            PlanetProgressCatalog.all[
                planetIndex
            ]

        return planetProgress[
            planetName
        ]?.unlocked == true
    }

    fun getCurrentPlanetIndex(): Int =
        currentPlanetIndex

    fun getCurrentPlanet(): Planet? =
        state.currentPlanet

    fun getCurrentPlanetMission() =
        PlanetMissionCatalog.getForPlanet(
            state.currentPlanet?.name ?: "Luna"
        )

    fun selectPlanet(
        planetIndex: Int
    ): Boolean {

        if (
            planetIndex !in
            Planets.all.indices
        ) {
            return false
        }

        if (
            !isPlanetUnlocked(
                planetIndex
            )
        ) {
            return false
        }

        if (
            state.gamePhase !=
            GameState.GamePhase.AWAITING_FIRST_TAP &&
            state.gamePhase !=
            GameState.GamePhase.LANDED_SUCCESS &&
            state.gamePhase !=
            GameState.GamePhase.LANDED_FAILED
        ) {
            return false
        }

        currentPlanetIndex =
            planetIndex

        state.currentPlanet =
            Planets.all[
                planetIndex
            ]

        prepareRocket()

        flightTime =
            0f

        landingStartedAltitude =
            0f

        state.gamePhase =
            GameState.GamePhase.AWAITING_FIRST_TAP

        savePlanetProgress()

        return true
    }

    private fun completePlanetMission() {

        val planetName =
            PlanetProgressCatalog.all[
                currentPlanetIndex
            ]

        val currentProgress =
            planetProgress.getOrPut(
                planetName
            ) {
                PlanetProgress(
                    planetName = planetName
                )
            }

        currentProgress.missionsCompleted++

        val nextIndex =
            currentPlanetIndex + 1

        if (
            nextIndex <
            PlanetProgressCatalog.all.size
        ) {

            val nextPlanetName =
                PlanetProgressCatalog.all[
                    nextIndex
                ]

            val nextProgress =
                planetProgress.getOrPut(
                    nextPlanetName
                ) {
                    PlanetProgress(
                        planetName = nextPlanetName
                    )
                }

            nextProgress.unlocked = true

            currentPlanetIndex =
                nextIndex
        }

        savePlanetProgress()
    }

    private fun prepareRocket() {

        val template =
            getSelectedRocket()

        state.rocket =
            template.copy(
                fuel =
                    template.maxFuel,

                altitude =
                    0f,

                velocity =
                    0f,

                acceleration =
                    0f,

                stage =
                    0,

                unlocked =
                    true
            )

        state.missionReward =
            0
    }

    private fun choosePlanet() {

        if (
            currentPlanetIndex !in
            Planets.all.indices
        ) {
            currentPlanetIndex = 0
        }

        state.currentPlanet =
            Planets.all[
                currentPlanetIndex
            ]
    }

    fun handleTap() {

        when (
            state.gamePhase
        ) {

            GameState.GamePhase.AWAITING_FIRST_TAP -> {

                state.gamePhase =
                    GameState.GamePhase.STAGE_1_ACTIVE

                state.rocket.stage =
                    1
            }

            GameState.GamePhase.STAGE_1_ACTIVE -> {

                state.gamePhase =
                    GameState.GamePhase.STAGE_2_ACTIVE

                state.rocket.stage =
                    2
            }

            GameState.GamePhase.STAGE_2_ACTIVE -> {

                state.gamePhase =
                    GameState.GamePhase.STAGE_3_ACTIVE

                state.rocket.stage =
                    3
            }

            GameState.GamePhase.LANDED_SUCCESS,
            GameState.GamePhase.LANDED_FAILED -> {

                reset()
            }

            else -> Unit
        }
    }

    fun update(
        deltaTime: Float
    ) {

        val dt =
            deltaTime.coerceIn(
                0f,
                0.05f
            )

        when (
            state.gamePhase
        ) {

            GameState.GamePhase.STAGE_1_ACTIVE ->
                launch(
                    dt,
                    1
                )

            GameState.GamePhase.STAGE_2_ACTIVE ->
                launch(
                    dt,
                    2
                )

            GameState.GamePhase.STAGE_3_ACTIVE -> {

                launch(
                    dt,
                    3
                )

                if (
                    state.rocket.altitude >=
                    40f
                ) {
                    state.gamePhase =
                        GameState.GamePhase.IN_FLIGHT
                }
            }

            GameState.GamePhase.IN_FLIGHT ->
                fly(dt)

            GameState.GamePhase.LANDING_SEQUENCE ->
                landing(dt)

            else -> Unit
        }
    }

    private fun launch(
        deltaTime: Float,
        stage: Int
    ) {

        val rocket =
            state.rocket

        val mission =
            getCurrentPlanetMission()

        val step =
            FlightController.launch(
                rocket =
                    rocket,

                mission =
                    mission,

                stage =
                    stage,

                deltaTime =
                    deltaTime
            )

        rocket.acceleration =
            step.acceleration

        rocket.velocity =
            step.velocity

        rocket.altitude =
            step.altitude

        rocket.fuel =
            step.fuel

        if (
            step.fuelStatus.isEmpty
        ) {
            startLanding()
            return
        }

        if (
            rocket.altitude >=
            rocket.maxAltitude
        ) {
            startLanding()
        }
    }

    private fun fly(
        deltaTime: Float
    ) {

        val rocket =
            state.rocket

        val planet =
            state.currentPlanet
                ?: return

        val mission =
            getCurrentPlanetMission()

        flightTime +=
            deltaTime

        val step =
            FlightController.fly(
                rocket =
                    rocket,

                mission =
                    mission,

                deltaTime =
                    deltaTime
            )

        rocket.acceleration =
            step.acceleration

        rocket.velocity =
            step.velocity

        rocket.altitude =
            step.altitude

        rocket.fuel =
            step.fuel

        if (
            rocket.altitude >=
            planet.targetAltitude ||
            step.fuelStatus.isEmpty ||
            flightTime >=
            FlightBalance.FLIGHT_TIME_LIMIT
        ) {
            startLanding()
        }
    }

    private fun startLanding() {

        if (
            state.gamePhase !=
            GameState.GamePhase.LANDING_SEQUENCE
        ) {

            landingStartedAltitude =
                state.rocket.altitude

            state.gamePhase =
                GameState.GamePhase.LANDING_SEQUENCE

            state.rocket.velocity =
                state.rocket.velocity
                    .coerceAtLeast(
                        25f
                    )
        }
    }

    private fun calculateMissionReward(): Int {

        val planet =
            state.currentPlanet
                ?: return 0

        val mission =
            getCurrentPlanetMission()

        val rocket =
            state.rocket

        val upgrade =
            rocketUpgrades[
                selectedRocketId
            ]

        val engineLevel =
            upgrade?.engineLevel ?: 0

        val fuelLevel =
            upgrade?.fuelLevel ?: 0

        val altitudeLevel =
            upgrade?.altitudeLevel ?: 0

        return EconomyBalance.missionReward(
            planetReward =
                planet.reward,

            rocketId =
                selectedRocketId,

            engineLevel =
                engineLevel,

            fuelLevel =
                fuelLevel,

            altitudeLevel =
                altitudeLevel,

            altitude =
                rocket.altitude,

            missionRewardMultiplier =
                mission.rewardMultiplier
        )
    }

    private fun landing(
        deltaTime: Float
    ) {

        val rocket =
            state.rocket

        val dt =
            deltaTime.coerceIn(
                0f,
                0.05f
            )

        val acceleration =
            FlightController.landingAcceleration(
                landingStartedAltitude
            )

        rocket.acceleration =
            acceleration

        rocket.velocity +=
            acceleration *
                    dt

        rocket.velocity =
            rocket.velocity.coerceAtLeast(
                -FlightBalance.MAX_LANDING_VELOCITY
            )

        rocket.altitude +=
            rocket.velocity *
                    dt

        if (
            rocket.altitude > 0f
        ) {
            return
        }

        rocket.altitude =
            0f

        val safeVelocity =
            FlightController.landingVelocityLimit(
                landingStartedAltitude
            )

        if (
            kotlin.math.abs(
                rocket.velocity
            ) <=
            safeVelocity
        ) {

            state.gamePhase =
                GameState.GamePhase.LANDED_SUCCESS

            val reward =
                calculateMissionReward()

            state.missionReward =
                reward

            state.totalCoins +=
                reward

            completePlanetMission()

        } else {

            state.gamePhase =
                GameState.GamePhase.LANDED_FAILED

            state.missionReward =
                0
        }

        saveProgress()
    }

    fun reset() {

        prepareRocket()

        flightTime =
            0f

        landingStartedAltitude =
            0f

        choosePlanet()

        state.gamePhase =
            GameState.GamePhase.AWAITING_FIRST_TAP
    }
}
