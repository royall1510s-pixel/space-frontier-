package com.spacefrontier.game.models

data class Rocket(
    val name: String = "Explorer I",
    var fuel: Float = 100f,
    var altitude: Float = 0f,
    var velocity: Float = 0f,
    var acceleration: Float = 0f,
    var stage: Int = 0
)
