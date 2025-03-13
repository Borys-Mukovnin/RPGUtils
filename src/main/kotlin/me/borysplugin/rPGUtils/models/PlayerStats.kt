package me.borysplugin.rPGUtils.models

data class PlayerStats(
    val playerName: String = "Unknown",
    val health: Double = 20.0,
    val attackPower: Double = 0.0,
    val attackSpeed: Double = 0.0,
    val defense: Double = 0.0,
    val critChance: Double = 10.0,
    val critDamage: Double = 0.0
)
