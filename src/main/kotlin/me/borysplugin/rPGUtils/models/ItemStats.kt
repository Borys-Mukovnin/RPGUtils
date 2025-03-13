package me.borysplugin.rPGUtils.models

import org.bukkit.Material

data class ItemStats(
    val displayName: String = "Unknown Item",
    val material: Material = Material.AIR,
    val health: Double = 20.0,
    val attackPower: Double = 0.0,
    val attackSpeed: Double = 0.0,
    val defense: Double = 0.0,
    val critChance: Double = 10.0,
    val critDamage: Double = 0.0,
    val allowOffhand: Boolean = false,
    val lore: List<String> = emptyList()
)
