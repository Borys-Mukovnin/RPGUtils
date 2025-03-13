package me.borysplugin.rPGUtils.events

import me.borysplugin.rPGUtils.PluginUtils
import me.borysplugin.rPGUtils.RPGUtils
import me.borysplugin.rPGUtils.managers.CombatManager
import me.borysplugin.rPGUtils.managers.PlayerManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import kotlin.random.Random

class CombatListener(
    private val plugin: RPGUtils,
    private val playerManager: PlayerManager,
    private val combatManager: CombatManager
) : Listener {
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
            val damageMultiplier = 1.0 - ((defenderDefense / (defenderDefense + finalDamage)) * 0.8)
            finalDamage = damageMultiplier * finalDamage
            event.damage = finalDamage
            return
        }

        // Mark player as in combat (to trigger immediate updates)

        combatManager.SetPlayerInCombat(attacker.uniqueId)

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

        if (PluginUtils.isCriticalHit(attacker)) {

            if (attackerCritChance!! > Random.nextDouble(100.0)) {

                finalDamage = attackerAttackPower!! * ((attackerCritDamage!! + 100) / 100)
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
            val damageMultiplier = 1.0 - ((defenderDefense / (defenderDefense + finalDamage)) * 0.8)
            finalDamage = damageMultiplier * finalDamage
        }

        val location = event.entity.location
        combatManager.showDamageHologram(plugin,location,finalDamage)
        event.damage = finalDamage
    }
}