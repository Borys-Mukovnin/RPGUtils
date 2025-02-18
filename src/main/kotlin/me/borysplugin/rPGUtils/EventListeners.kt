package me.borysplugin.rPGUtils

import org.bukkit.Bukkit
import kotlin.random.Random
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.scheduler.BukkitTask
import java.util.*

class EventListeners(
    private val plugin: RPGUtils,
    private val playerManager: PlayerManager,
    private val combatManager: CombatManager
) : Listener {

    private val updateTasks: MutableMap<UUID, BukkitTask> = mutableMapOf()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        playerManager.UpdateOnlinePlayers(event)
        setMaxHealth(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        playerManager.UpdateOnlinePlayers(event)
    }
    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player
        val defender = event.entity as? Player

        if (attacker !is Player && defender !is Player) return

        if (attacker !is Player) {
            defender!!

            var finalDamage = event.damage

            val finalStatsDefender = playerManager.GetPlayerTotalStats(defender.uniqueId)
            val defenderDefense = finalStatsDefender!!.defense
            val mitigationFactor = (defenderDefense / (finalDamage + defenderDefense)) * 0.8
            val damageMultiplier = 1.0 - mitigationFactor
            finalDamage *= damageMultiplier
            event.damage = finalDamage
            return
        }

        // Mark player as in combat (to trigger immediate updates)
        if (true) {
            combatManager.SetPlayerInCombat(attacker.uniqueId)
        }
        if (defender is Player) {
            combatManager.SetPlayerInCombat(defender.uniqueId)
        }

        // Ensure stats are updated before applying damage

        // Now retrieve and apply the updated stats
        val finalStatsAttacker = playerManager.GetPlayerTotalStats(attacker.uniqueId)

        val attackerAttackPower = finalStatsAttacker?.attackPower
        val attackerCritChance = finalStatsAttacker?.critChance
        val attackerCritDamage = finalStatsAttacker?.critDamage

        var finalDamage = 0.0

        if (isCriticalHit(attacker)) {

            if (Random.nextDouble(100.0) < attackerCritChance!!) {

                finalDamage = attackerAttackPower!! * ((100 + attackerCritDamage!!) / 100)
                attacker.sendMessage("§6Critical Hit!")
            } else {
                finalDamage = attackerAttackPower!!
            }
        } else {
            finalDamage = attackerAttackPower!!
        }
        if (finalDamage == 0.0) finalDamage += 1.0

        if (defender is Player) {
            val finalStatsDefender = playerManager.GetPlayerTotalStats(defender.uniqueId)
            val defenderDefense = finalStatsDefender!!.defense
            val mitigationFactor = (defenderDefense / (finalDamage + defenderDefense)) * 0.8
            val damageMultiplier = 1.0 - mitigationFactor
            finalDamage *= damageMultiplier
        }

        val location = event.entity.location
        combatManager.showDamageHologram(plugin,location,finalDamage)
        event.damage = finalDamage
    }

    @EventHandler
    fun onItemHeldChange(event: PlayerItemHeldEvent) {
        val player = event.player
        scheduleStatUpdate(event, player)
        setMaxHealth(player)
    }

    @EventHandler
    fun onSwapHandItems(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        scheduleStatUpdate(event, player)
        setMaxHealth(player)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        scheduleStatUpdate(event, player)
        setMaxHealth(player)
    }


    fun scheduleStatUpdate(event: org.bukkit.event.Event, player: Player) {
        //val uuid = player.uniqueId

        Bukkit.getScheduler().runTask(plugin, Runnable {
            playerManager.UpdateOnlinePlayers(event)
        })

    }
    fun isCriticalHit(player: Player): Boolean {

        return !player.isOnGround
                && !player.isSprinting
                && player.velocity.y < 0.0 // Indicates falling
                && !player.location.block.isLiquid
                && player.fallDistance > 0.0 // Ensures falling
    }
    fun setMaxHealth(player: Player) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val totalStats = playerManager.GetPlayerTotalStats(player.uniqueId)
            player.maxHealth = totalStats!!.health
            player.health = player.maxHealth
        })
    }
}
