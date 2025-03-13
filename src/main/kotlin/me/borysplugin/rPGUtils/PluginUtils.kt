package me.borysplugin.rPGUtils

import com.sun.source.util.Plugin
import me.borysplugin.rPGUtils.managers.PlayerManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

object PluginUtils {

    fun setMaxHealth(player: Player, amount: Double, plugin:RPGUtils) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            player.maxHealth = amount
            player.health = player.maxHealth
        })
    }

    fun isCriticalHit(player: Player): Boolean {

        return !player.isOnGround
                && !player.isSprinting
                && player.velocity.y < 0.0 // Indicates falling
                && !player.location.block.isLiquid
                && player.fallDistance > 0.0 // Ensures falling
    }

    fun createItem(material: Material, name: String, lore: List<String>, amount: Int): ItemStack {
        val item = ItemStack(material, amount)
        val meta: ItemMeta? = item.itemMeta
        meta?.setDisplayName(name)
        meta?.lore = lore
        meta?.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        item.itemMeta = meta
        return item
    }

    fun scheduleStatUpdate(plugin: RPGUtils, event: org.bukkit.event.Event, playerManager: PlayerManager) {
        //val uuid = player.uniqueId

        Bukkit.getScheduler().runTask(plugin, Runnable {
            playerManager.UpdateOnlinePlayers(event)
        })
    }
}