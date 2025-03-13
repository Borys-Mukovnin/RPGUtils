package me.borysplugin.rPGUtils.gui.ItemsGUIs

import me.borysplugin.rPGUtils.PluginUtils
import me.borysplugin.rPGUtils.RPGUtils
import me.borysplugin.rPGUtils.interfaces.IOpenable
import me.borysplugin.rPGUtils.managers.ItemsManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class ItemsGUI(private val plugin: RPGUtils,private val itemsManager: ItemsManager) : Listener,IOpenable {

    override val title = "Items editor"
    override val size = 54 // Must be a multiple of 9 (9, 18, 27, 36, 45, 54)
    override val gui = Bukkit.createInventory(null,size, title)

    init {
        plugin.server.pluginManager.registerEvents(this,plugin)
    }

    override fun setupItems() {

        val items = PluginUtils.createItem(
            Material.IRON_SWORD,
            "Items",
            listOf(
                "Left Click to show all items"
            ),
            1
        )

        gui.setItem(15,items)


    }

    override fun open(player: Player) {
        player.openInventory(gui)
    }

    @EventHandler
    override fun onInventoryClick(event: InventoryClickEvent) {

        when (event.view.title) {
            this.title -> {
                event.isCancelled = true // Prevent item removal

                val player = event.whoClicked as? Player ?: return
                val clickedItem = event.currentItem ?: return
                val slot = event.slot
                val clickType = event.click
                if (clickType != ClickType.LEFT ) return

                when(slot) {
                    15 -> {
                        val itemsAllGUI = ItemsAllGUI(plugin,itemsManager)

                        itemsAllGUI.setupItems()
                        itemsAllGUI.open(player)
                    }
                }


            }
        }
    }
}
