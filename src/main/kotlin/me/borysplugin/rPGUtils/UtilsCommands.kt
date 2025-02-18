package me.borysplugin.rPGUtils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class UtilsCommands : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player || !sender.isOp) {
            sender.sendMessage("${ChatColor.RED}You do not have permission to use this command.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}Usage: /utils <command> [args]")
            return true
        }

        when (args[0].lowercase()) {
            "heal" -> healCommand(sender, args.drop(1))
            "feed" -> feedCommand(sender, args.drop(1))
            else -> sender.sendMessage("${ChatColor.RED}Unknown command.")
        }
        return true
    }

    private fun healCommand(sender: CommandSender, args: List<String>) {
        val target: Player? = if (args.size > 1) Bukkit.getPlayer(args[1]) else if (sender is Player) sender else null
        val healAmount: Double = args.firstOrNull()?.toDoubleOrNull() ?: target?.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0

        if (target == null) {
            sender.sendMessage("${ChatColor.RED}Player not found.")
            return
        }

        target.health = (target.health + healAmount).coerceAtMost(target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0)
        target.sendMessage("${ChatColor.GREEN}You have been healed by ${sender.name}.")
        sender.sendMessage("${ChatColor.GREEN}Healed ${target.name} by $healAmount health.")
    }

    private fun feedCommand(sender: CommandSender, args: List<String>) {
        val target: Player? = if (args.isNotEmpty()) Bukkit.getPlayer(args[0]) else if (sender is Player) sender else null

        if (target == null) {
            sender.sendMessage("${ChatColor.RED}Player not found.")
            return
        }

        target.foodLevel = 20
        target.saturation = 5.0f
        target.sendMessage("${ChatColor.YELLOW}You have been fed by ${sender.name}.")
        sender.sendMessage("${ChatColor.YELLOW}Fed ${target.name}.")
    }
}


