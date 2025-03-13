package me.borysplugin.rPGUtils.gui.ItemsGUIs

import com.sun.source.util.Plugin
import me.borysplugin.rPGUtils.PluginUtils
import me.borysplugin.rPGUtils.RPGUtils
import me.borysplugin.rPGUtils.interfaces.IOpenable
import me.borysplugin.rPGUtils.managers.ItemsManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

class ItemsAllGUI(private val plugin: RPGUtils, private val itemsManager: ItemsManager) : Listener, IOpenable {
    override val title: String
        get() = "Items List"
    override val size: Int
        get() = 54
    override val gui = Bukkit.createInventory(null, size, title)

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    var slot = 0
    override fun setupItems() {
        itemsManager.itemsMap.forEach { map ->
            val category = map.key
            map.value.forEach { item ->
                val itemName = item.key

                val item = itemsManager.CreateItem(category,itemName)
                val meta: ItemMeta? = item?.itemMeta
                if (meta != null) {
                    val t_lore = meta.lore?.toList()?.plus(listOf("LC","RC"))
                    meta.lore = t_lore // Set the lore
                    item.itemMeta = meta // Apply the changes
                }

                gui.setItem(slot, item)
                slot += 1
            }
        }
    }

    override fun open(player: Player) {
        player.openInventory(gui)
    }

    @EventHandler
    override fun onInventoryClick(event: InventoryClickEvent) {
        if (event.view.title != this.title) return

        event.isCancelled = true // Prevent item removal

        val player = event.whoClicked as? Player ?: return
        val clickedItem = event.currentItem ?: return
        val slot = event.slot
        val clickType = event.click

        when (slot) {
            in 0..54 -> {
                val pdc = clickedItem.itemMeta?.persistentDataContainer

                if (clickType == ClickType.LEFT) {
                    val modifyItemGUI = ModifyItemGUI(plugin,itemsManager,clickedItem)

                    modifyItemGUI.setupItems()
                    modifyItemGUI.open(player)
                }
                if (clickType == ClickType.RIGHT) {
                    player.sendMessage(pdc?.get(NamespacedKey(plugin, "itemname"), PersistentDataType.STRING))
                }
            }
        }


    }
}