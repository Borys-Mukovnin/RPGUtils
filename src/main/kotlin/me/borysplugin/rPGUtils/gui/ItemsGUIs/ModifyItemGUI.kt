package me.borysplugin.rPGUtils.gui.ItemsGUIs

import com.sun.source.util.Plugin
import me.borysplugin.rPGUtils.PluginUtils
import me.borysplugin.rPGUtils.RPGUtils
import me.borysplugin.rPGUtils.interfaces.IOpenable
import me.borysplugin.rPGUtils.managers.ItemsManager
import me.borysplugin.rPGUtils.models.ItemStats
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ModifyItemGUI(
    private val plugin: RPGUtils,
    private val itemsManager: ItemsManager,
    private val item: ItemStack
) : IOpenable,Listener {
    override val title = "Modify Item"
    override val size = 54
    override val gui = Bukkit.createInventory(null,size, title)

    val pdc = item.itemMeta?.persistentDataContainer
    val itemName = pdc?.get(NamespacedKey(plugin, "itemname"), PersistentDataType.STRING)
    val itemCategory = pdc?.get(NamespacedKey(plugin, "category"), PersistentDataType.STRING)
    val itemStats = itemsManager.GetItem(itemCategory!!,itemName!!)

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun setupItems() {

        val materialItem = PluginUtils.createItem(
            Material.IRON_SWORD,
            "Material: ${itemStats?.material}",
            listOf(
                "Left click to modify",
                "Right click to reset"
            ),
            1
        )

        gui.setItem(10,materialItem)

        val displayNameItem = PluginUtils.createItem(
            Material.IRON_SWORD,
            "Display name: ${itemStats?.displayName}",
            listOf(
                "Left click to modify",
                "Right click to reset"
            ),
            1
        )
        gui.setItem(11,displayNameItem)

        val loreItem = PluginUtils.createItem(
            Material.IRON_SWORD,
            "Lore: ${itemStats?.lore}",
            listOf(
                "Left click to modify",
                "Right click to reset"
            ),
            1
        )
        gui.setItem(12,loreItem)

        val attackPowerItem = PluginUtils.createItem(
            Material.IRON_SWORD,
            "Attack power: ${itemStats?.attackPower}",
            listOf(
                "Left click to modify",
                "Right click to reset"
            ),
            1
        )
        gui.setItem(13,attackPowerItem)

        val critChanceItem = PluginUtils.createItem(
            Material.WOODEN_SWORD,
            "Crit chance: ${itemStats?.critChance}",
            listOf(
                "Left click to modify",
                "Right click to reset"
            ),
            1
        )
        gui.setItem(14,critChanceItem)

        val critDamageItem = PluginUtils.createItem(
            Material.WOODEN_SWORD,
            "Crit damage: ${itemStats?.critDamage}",
            listOf(
                "Left click to modify",
                "Right click to reset"
            ),
            1
        )
        gui.setItem(15,critDamageItem)

        val healthItem = PluginUtils.createItem(
            Material.WOODEN_SWORD,
            "Health: ${itemStats?.health}",
            listOf(
                "Left click to modify",
                "Right click to reset"
            ),
            1
        )
        gui.setItem(16,healthItem)

        val defenseItem = PluginUtils.createItem(
            Material.WOODEN_SWORD,
            "Defense: ${itemStats?.defense}",
            listOf(
                "Left click to modify",
                "Right click to reset"
            ),
            1
        )
        gui.setItem(19,defenseItem)

        val allowOffhandItem = PluginUtils.createItem(
            Material.WOODEN_SWORD,
            "Allow offhand: ${itemStats?.allowOffhand}",
            listOf(
                "Left click to modify",
                "Right click to reset"
            ),
            1
        )
        gui.setItem(20  ,allowOffhandItem)
    }

    override fun open(player: Player) {
        player.openInventory(gui)
    }

    @EventHandler
    override fun onInventoryClick(event: InventoryClickEvent) {
        TODO("Not yet implemented")
    }
}