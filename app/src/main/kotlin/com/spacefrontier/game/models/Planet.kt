package com.spacefrontier.game.models

data class Planet(
    val name: String,
    val emoji: String,
    val targetAltitude: Float,
    val reward: Int
)

object Planets {

    val all = listOf(
        Planet("Luna", "🌙", 120f, 100),
        Planet("Mars", "🔴", 180f, 250),
        Planet("Europa", "🔵", 240f, 450),
        Planet("Titan", "🟠", 320f, 700),
        Planet("Neptune", "🔵", 450f, 1200)
    )
}
