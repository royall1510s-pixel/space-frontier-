package com.spacefrontier.game.models

data class Rocket(
    val id: Int = 1,
    val name: String = "Explorer I",
    val price: Int = 0,
    val maxFuel: Float = 100f,
    val thrust: Float = 35f,
    val maxAltitude: Float = 120f,
    var fuel: Float = 100f,
    var altitude: Float = 0f,
    var velocity: Float = 0f,
    var acceleration: Float = 0f,
    var stage: Int = 0,
    var unlocked: Boolean = true
)

object RocketCatalog {

    val all = listOf(

        Rocket(
            id = 1,
            name = "Explorer I",
            price = 0,
            maxFuel = 100f,
            thrust = 35f,
            maxAltitude = 120f,
            fuel = 100f
        ),

        Rocket(
            id = 2,
            name = "Falcon",
            price = 500,
            maxFuel = 115f,
            thrust = 42f,
            maxAltitude = 150f,
            fuel = 115f,
            unlocked = false
        ),

        Rocket(
            id = 3,
            name = "Titan",
            price = 1000,
            maxFuel = 130f,
            thrust = 50f,
            maxAltitude = 180f,
            fuel = 130f,
            unlocked = false
        ),

        Rocket(
            id = 4,
            name = "Voyager",
            price = 1800,
            maxFuel = 145f,
            thrust = 58f,
            maxAltitude = 210f,
            fuel = 145f,
            unlocked = false
        ),

        Rocket(
            id = 5,
            name = "Orion",
            price = 2800,
            maxFuel = 160f,
            thrust = 66f,
            maxAltitude = 240f,
            fuel = 160f,
            unlocked = false
        ),

        Rocket(
            id = 6,
            name = "Phoenix",
            price = 4000,
            maxFuel = 175f,
            thrust = 74f,
            maxAltitude = 270f,
            fuel = 175f,
            unlocked = false
        ),

        Rocket(
            id = 7,
            name = "Atlas",
            price = 5500,
            maxFuel = 190f,
            thrust = 82f,
            maxAltitude = 300f,
            fuel = 190f,
            unlocked = false
        ),

        Rocket(
            id = 8,
            name = "Dragon",
            price = 7500,
            maxFuel = 210f,
            thrust = 90f,
            maxAltitude = 340f,
            fuel = 210f,
            unlocked = false
        ),

        Rocket(
            id = 9,
            name = "Nova",
            price = 10000,
            maxFuel = 230f,
            thrust = 100f,
            maxAltitude = 380f,
            fuel = 230f,
            unlocked = false
        ),

        Rocket(
            id = 10,
            name = "Starlight",
            price = 13000,
            maxFuel = 250f,
            thrust = 110f,
            maxAltitude = 420f,
            fuel = 250f,
            unlocked = false
        ),

        Rocket(
            id = 11,
            name = "Nebula",
            price = 16500,
            maxFuel = 275f,
            thrust = 120f,
            maxAltitude = 460f,
            fuel = 275f,
            unlocked = false
        ),

        Rocket(
            id = 12,
            name = "Eclipse",
            price = 20500,
            maxFuel = 300f,
            thrust = 130f,
            maxAltitude = 500f,
            fuel = 300f,
            unlocked = false
        ),

        Rocket(
            id = 13,
            name = "Galaxy",
            price = 25000,
            maxFuel = 330f,
            thrust = 140f,
            maxAltitude = 550f,
            fuel = 330f,
            unlocked = false
        ),

        Rocket(
            id = 14,
            name = "Horizon",
            price = 30000,
            maxFuel = 360f,
            thrust = 150f,
            maxAltitude = 600f,
            fuel = 360f,
            unlocked = false
        ),

        Rocket(
            id = 15,
            name = "Pioneer",
            price = 36000,
            maxFuel = 390f,
            thrust = 160f,
            maxAltitude = 650f,
            fuel = 390f,
            unlocked = false
        ),

        Rocket(
            id = 16,
            name = "Infinity",
            price = 43000,
            maxFuel = 425f,
            thrust = 175f,
            maxAltitude = 700f,
            fuel = 425f,
            unlocked = false
        ),

        Rocket(
            id = 17,
            name = "Quantum",
            price = 51000,
            maxFuel = 460f,
            thrust = 190f,
            maxAltitude = 760f,
            fuel = 460f,
            unlocked = false
        ),

        Rocket(
            id = 18,
            name = "Supernova",
            price = 60000,
            maxFuel = 500f,
            thrust = 210f,
            maxAltitude = 820f,
            fuel = 500f,
            unlocked = false
        ),

        Rocket(
            id = 19,
            name = "Cosmos",
            price = 72000,
            maxFuel = 550f,
            thrust = 230f,
            maxAltitude = 900f,
            fuel = 550f,
            unlocked = false
        ),

        Rocket(
            id = 20,
            name = "Space Frontier",
            price = 100000,
            maxFuel = 620f,
            thrust = 260f,
            maxAltitude = 1000f,
            fuel = 620f,
            unlocked = false
        )
    )
}
