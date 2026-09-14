package com.spacefrontier.game.models

data class GameState(
    var rocket: Rocket = Rocket(),
    var currentPlanet: Planet? = null,
    var totalCoins: Int = 0,
    var missionReward: Int = 0,
    var gamePhase: GamePhase =
        GamePhase.AWAITING_FIRST_TAP
) {

    enum class GamePhase {

        AWAITING_FIRST_TAP,

        STAGE_1_ACTIVE,

        STAGE_2_ACTIVE,

        STAGE_3_ACTIVE,

        IN_FLIGHT,

        LANDING_SEQUENCE,

        LANDED_SUCCESS,

        LANDED_FAILED
    }
}
