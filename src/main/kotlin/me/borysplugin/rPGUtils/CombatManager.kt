package me.borysplugin.rPGUtils

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.ArmorStand
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import java.util.UUID
import java.util.concurrent.TimeUnit

class CombatManager(
    private val plugin: RPGUtils,
    private val playerManager: PlayerManager
) {

    private val combatTimer: MutableMap<UUID, Long> = mutableMapOf()
    private val combatDuration: Long = TimeUnit.SECONDS.toMillis(10) // 10 seconds combat duration

    // Function to set player in combat
    fun SetPlayerInCombat(uuid: UUID) {
        val currentTime = System.currentTimeMillis()
        combatTimer[uuid] = currentTime

        // Schedule a task to remove the player from combat after 10 seconds
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            RemovePlayerFromCombat(uuid)
        }, combatDuration / 50) // Convert milliseconds to ticks (1 tick = 50ms)
    }

    // Function to check if a player is in combat
    fun IsPlayerInCombat(uuid: UUID): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastCombatTime = combatTimer[uuid] ?: return false

        // If the player was last in combat within the last 10 seconds, they are in combat
        return currentTime - lastCombatTime <= combatDuration
    }

    // Function to remove the player from combat
    private fun RemovePlayerFromCombat(uuid: UUID) {
        combatTimer.remove(uuid)
    }

    fun showActionBar(player: Player) {
        val uuid = player.uniqueId
        val health = playerManager.GetPlayerTotalStats(uuid)?.health
        val defense = playerManager.GetPlayerTotalStats(uuid)?.defense
        val inCombat = IsPlayerInCombat(uuid)

        val combatTimeRemaining = if (inCombat) {
            val remainingTime = combatDuration - (System.currentTimeMillis() - combatTimer[uuid]!!)
            val secondsRemaining = (remainingTime / 1000).toInt()
            "§cIn Combat ($secondsRemaining s)"
        } else {
            "Out of Combat"
        }

        // Constructing the styled action bar message
        val message = TextComponent("Health: $health | Defense: $defense | $combatTimeRemaining")
        message.color = net.md_5.bungee.api.ChatColor.GREEN // Set the color of the text

        // Send the action bar message
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(message))
    }


    // Function to update all players' action bars periodically
    fun startActionBarUpdates() {
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getOnlinePlayers().forEach { player ->
                    showActionBar(player)
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // Update every second (20 ticks)
    }

    fun showDamageHologram(plugin: RPGUtils, location: Location, damage: Double) {
        val world = location.world ?: return
        val armorStand = world.spawnEntity(location.add(0.0, 1.5, 0.0), EntityType.ARMOR_STAND) as ArmorStand

        armorStand.isVisible = false
        armorStand.isMarker = true
        armorStand.setGravity(false)
        armorStand.customName = "§c-$damage ❤"
        armorStand.isCustomNameVisible = true

        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard ?: return
        val team = scoreboard.getTeam("invisible") ?: scoreboard.registerNewTeam("invisible")

        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
        team.addEntry(armorStand.uniqueId.toString())

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            armorStand.remove()
        }, 30L)
    }
}

