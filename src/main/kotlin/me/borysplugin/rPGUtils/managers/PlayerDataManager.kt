package me.borysplugin.rPGUtils.managers

import me.borysplugin.rPGUtils.RPGUtils
import me.borysplugin.rPGUtils.models.PlayerStats
import me.borysplugin.rPGUtils.storage.DatabaseManager
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

class PlayerDataManager(private val plugin: RPGUtils, private val mySQLManager: DatabaseManager) {

    private val onlinePlayersData: MutableMap<UUID, PlayerStats> = mutableMapOf()

    fun ReadPlayersFromFile(uuid: UUID): PlayerStats {
        CreatePlayerDataFile(uuid)

        val playerStats = mySQLManager.loadPlayerStatsFromDB(uuid)
        if (playerStats != null) {
            return playerStats
        } else {
            // If player stats are not found in the database, create them
            val player = plugin.server.getPlayer(uuid)
            mySQLManager.createPlayerStatsInDB(uuid, player?.name ?: "Unknown")

            // Return default stats if the player doesn't exist in DB
            return PlayerStats(
                playerName = player?.name ?: "Unknown",
                health = 20.0,
                attackPower = 0.0,
                attackSpeed = 0.0,
                defense = 0.0,
                critChance = 10.0,
                critDamage = 0.0
            )
        }
    }


    fun CreatePlayerDataFile(uuid: UUID) {
        val playerFile = File(plugin.dataFolder, "players/$uuid.yml")
        if (playerFile.exists()) return

        val config = YamlConfiguration()
        val player = plugin.server.getPlayer(uuid)

        config.set("uuid.playerName", player?.name ?: "Unknown")
        config.set("uuid.health", 20.0)
        config.set("uuid.attackPower", 0.0)
        config.set("uuid.attackSpeed", 0.0)
        config.set("uuid.defense", 0.0)
        config.set("uuid.critChance", 10.0)
        config.set("uuid.critDamage", 0.0)

        config.save(playerFile)
    }

    fun AddPlayerToList(uuid: UUID) {
        onlinePlayersData[uuid] = ReadPlayersFromFile(uuid)
    }

    fun RemovePlayerFromList(uuid: UUID) {
        onlinePlayersData.remove(uuid)
    }

    fun SavePlayerToFile(uuid: UUID) {
        val playerStats = onlinePlayersData[uuid] ?: return
        val playerFile = File(plugin.dataFolder, "players/$uuid.yml")
        val config = YamlConfiguration()

        config.set("$uuid.playerName", playerStats.playerName)
        config.set("$uuid.health", playerStats.health)
        config.set("$uuid.attackPower", playerStats.attackPower)
        config.set("$uuid.attackSpeed", playerStats.attackSpeed)
        config.set("$uuid.defense", playerStats.defense)
        config.set("$uuid.critChance", playerStats.critChance)
        config.set("$uuid.critDamage", playerStats.critDamage)

        config.save(playerFile)
    }

    fun GetPlayerStats(uuid: UUID): PlayerStats? {
        return onlinePlayersData[uuid]
    }

}
