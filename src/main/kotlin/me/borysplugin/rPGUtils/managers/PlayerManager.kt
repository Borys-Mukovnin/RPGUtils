package me.borysplugin.rPGUtils.managers

import me.borysplugin.rPGUtils.RPGUtils
import me.borysplugin.rPGUtils.models.PlayerStats
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import java.util.*

class PlayerManager(
    private val plugin: RPGUtils,
    private val playerDataManager: PlayerDataManager,
    private val itemsManager: ItemsManager
) : Listener {

    private val onlinePlayers: MutableMap<UUID, PlayerStats> = mutableMapOf()

    // Function to get total stats of a player by UUID
    fun GetPlayerTotalStats(uuid: UUID): PlayerStats? {
        return onlinePlayers[uuid]
    }

    // Function to update the online player's stats based on player data and item data
    fun UpdateOnlinePlayers(event: org.bukkit.event.Event) {

        if (event is PlayerJoinEvent) {
            val player = event.player
            val uuid = player.uniqueId


            //val playerStats = playerDataManager.GetPlayerStats(uuid)

            val playerStats = playerDataManager.ReadPlayersFromFile(uuid)
            playerDataManager.AddPlayerToList(uuid)


            // Combine the stats into a new PlayerStats object
            val totalStats = PlayerStats(
                playerName = playerStats.playerName,
                health = playerStats.health + itemsManager.GetTotalStatValue(player, "health"),
                attackPower = playerStats.attackPower + itemsManager.GetTotalStatValue(player, "attackPower"),
                attackSpeed = playerStats.attackSpeed + itemsManager.GetTotalStatValue(player, "attackSpeed"),
                defense = playerStats.defense + itemsManager.GetTotalStatValue(player, "defense"),
                critChance = playerStats.critChance + itemsManager.GetTotalStatValue(player, "critChance"),
                critDamage = playerStats.critDamage + itemsManager.GetTotalStatValue(player, "critDamage")
            )

            // Add to the onlinePlayers map
            onlinePlayers[uuid] = totalStats

        } else if (event is PlayerSwapHandItemsEvent) {
            val player = event.player
            val uuid = player.uniqueId


            //val playerStats = playerDataManager.GetPlayerStats(uuid)

            val playerStats = playerDataManager.GetPlayerStats(uuid)


            // Combine the stats into a new PlayerStats object
            val totalStats = PlayerStats(
                playerName = playerStats!!.playerName,
                health = playerStats.health + itemsManager.GetTotalStatValue(player, "health"),
                attackPower = playerStats.attackPower + itemsManager.GetTotalStatValue(player, "attackPower"),
                attackSpeed = playerStats.attackSpeed + itemsManager.GetTotalStatValue(player, "attackSpeed"),
                defense = playerStats.defense + itemsManager.GetTotalStatValue(player, "defense"),
                critChance = playerStats.critChance + itemsManager.GetTotalStatValue(player, "critChance"),
                critDamage = playerStats.critDamage + itemsManager.GetTotalStatValue(player, "critDamage")
            )

            // Add to the onlinePlayers map
            onlinePlayers[uuid] = totalStats

        }  else if (event is PlayerItemHeldEvent) {
            val player = event.player
            val uuid = player.uniqueId


            //val playerStats = playerDataManager.GetPlayerStats(uuid)

            val playerStats = playerDataManager.GetPlayerStats(uuid)


            // Combine the stats into a new PlayerStats object
            val totalStats = PlayerStats(
                playerName = playerStats!!.playerName,
                health = playerStats.health + itemsManager.GetTotalStatValue(player, "health"),
                attackPower = playerStats.attackPower + itemsManager.GetTotalStatValue(player, "attackPower"),
                attackSpeed = playerStats.attackSpeed + itemsManager.GetTotalStatValue(player, "attackSpeed"),
                defense = playerStats.defense + itemsManager.GetTotalStatValue(player, "defense"),
                critChance = playerStats.critChance + itemsManager.GetTotalStatValue(player, "critChance"),
                critDamage = playerStats.critDamage + itemsManager.GetTotalStatValue(player, "critDamage")
            )

            // Add to the onlinePlayers map
            onlinePlayers[uuid] = totalStats

        } else if (event is InventoryClickEvent) {
            val player = event.whoClicked as Player
            val uuid = player.uniqueId


            //val playerStats = playerDataManager.GetPlayerStats(uuid)

            val playerStats = playerDataManager.GetPlayerStats(uuid)


            // Combine the stats into a new PlayerStats object
            val totalStats = PlayerStats(
                playerName = playerStats!!.playerName,
                health = playerStats.health + itemsManager.GetTotalStatValue(player, "health"),
                attackPower = playerStats.attackPower + itemsManager.GetTotalStatValue(player, "attackPower"),
                attackSpeed = playerStats.attackSpeed + itemsManager.GetTotalStatValue(player, "attackSpeed"),
                defense = playerStats.defense + itemsManager.GetTotalStatValue(player, "defense"),
                critChance = playerStats.critChance + itemsManager.GetTotalStatValue(player, "critChance"),
                critDamage = playerStats.critDamage + itemsManager.GetTotalStatValue(player, "critDamage")
            )

            // Add to the onlinePlayers map
            onlinePlayers[uuid] = totalStats

        } else if (event is PlayerQuitEvent) {
            val player = event.player
            val uuid = player.uniqueId

            // Add to the onlinePlayers map
            onlinePlayers.remove(uuid)
            playerDataManager.RemovePlayerFromList(uuid)

        }
    }

}

