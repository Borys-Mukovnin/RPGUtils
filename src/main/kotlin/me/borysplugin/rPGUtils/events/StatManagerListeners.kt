package me.borysplugin.rPGUtils.events

import me.borysplugin.rPGUtils.PluginUtils
import me.borysplugin.rPGUtils.RPGUtils
import me.borysplugin.rPGUtils.managers.PlayerManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class StatManagerListeners(
    private val plugin: RPGUtils,
    private val playerManager: PlayerManager
) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        playerManager.UpdateOnlinePlayers(event)
        PluginUtils.setMaxHealth(player,playerManager.GetPlayerTotalStats(player.uniqueId)!!.health,plugin)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        playerManager.UpdateOnlinePlayers(event)
    }

    @EventHandler
    fun onItemHeldChange(event: PlayerItemHeldEvent) {
        val player = event.player
        PluginUtils.scheduleStatUpdate(plugin, event, playerManager)
        PluginUtils.setMaxHealth(player,playerManager.GetPlayerTotalStats(player.uniqueId)!!.health, plugin)
    }

    @EventHandler
    fun onSwapHandItems(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        PluginUtils.scheduleStatUpdate(plugin, event, playerManager)
        PluginUtils.setMaxHealth(player, playerManager.GetPlayerTotalStats(player.uniqueId)!!.health, plugin)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        PluginUtils.scheduleStatUpdate(plugin, event, playerManager)
        PluginUtils.setMaxHealth(player, playerManager.GetPlayerTotalStats(player.uniqueId)!!.health, plugin)
    }



}