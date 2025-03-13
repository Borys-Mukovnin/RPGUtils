package me.borysplugin.rPGUtils.interfaces

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

interface IOpenable {
    val title: String
    val size: Int
    val gui: Inventory

    fun setupItems()
    fun open(player: Player)

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) // Handle clicks
}
