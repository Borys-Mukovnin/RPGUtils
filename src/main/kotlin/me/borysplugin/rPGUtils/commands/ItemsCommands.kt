package me.borysplugin.rPGUtils.commands

import me.borysplugin.rPGUtils.RPGUtils
import me.borysplugin.rPGUtils.gui.ItemsGUIs.ItemsGUI
import me.borysplugin.rPGUtils.managers.ItemsManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ItemsCommands(
    private val plugin: RPGUtils,
    private val itemsManager: ItemsManager
) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp) {
            sender.sendMessage("You do not have permission to use this command.")
            return true
        }

        when (args.getOrNull(0)) {
            "reload" -> {
                itemsManager.LoadItems()
                sender.sendMessage("Items reloaded successfully.")
            }

            "give" -> {
                if (args.size < 3) {
                    sender.sendMessage("Usage: /items give [category] [itemname] [amount] [player]")
                    return true
                }
                val category = args[1]
                val itemName = args[2]
                val amount = args.getOrNull(3)?.toIntOrNull() ?: 1
                val playerName = args.getOrNull(4) ?: (sender as? Player)?.name

                if (playerName == null) {
                    sender.sendMessage("You must specify a player.")
                    return true
                }

                repeat(amount) { itemsManager.GiveItem(category, itemName, playerName) }
                sender.sendMessage("Gave $amount of $itemName from $category to $playerName.")
            }

            "gui" -> {
                val player = sender as? Player ?: run {
                    sender.sendMessage("Only players can execute this command.")
                    return false
                }

                val itemsGUI = ItemsGUI(plugin,itemsManager)

                itemsGUI.setupItems()
                itemsGUI.open(sender)
            }

            else -> sender.sendMessage("Invalid subcommand. Use /items reload or /items give.")
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        return when (args.size) {
            1 -> listOf("reload", "give", "gui").filter { it.startsWith(args[0], true) }
            2 -> if (args[0] == "give") itemsManager.itemsMap.keys.filter { it.startsWith(args[1], true) } else emptyList()
            3 -> itemsManager.itemsMap[args[1]]?.keys?.filter { it.startsWith(args[2], true) } ?: emptyList()
            4 -> listOf("1", "5", "10", "64").filter { it.startsWith(args[3], true) }
            5 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[4], true) }
            else -> emptyList()
        }
    }
}
