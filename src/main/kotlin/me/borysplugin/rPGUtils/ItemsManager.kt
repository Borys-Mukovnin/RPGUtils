package me.borysplugin.rPGUtils
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.io.File
import org.bukkit.NamespacedKey


class ItemsManager(private val plugin: RPGUtils) {

    val itemsMap: MutableMap<String, MutableMap<String, ItemStats>> = mutableMapOf()

    val categoryList: List<String> = listOf("swords", "armor")

    fun LoadItems() {
        val itemsFolder = File(plugin.dataFolder, "items")
        if (!itemsFolder.exists()) return

        itemsFolder.listFiles()?.forEach { categoryFile ->
            val categoryName = categoryFile.name.substringBeforeLast(".")
            if (categoryName !in categoryList) return@forEach  // Skip categories not in the list

            itemsMap[categoryName] = mutableMapOf()

            val config = YamlConfiguration.loadConfiguration(categoryFile)

            config.getKeys(false).forEach { itemName ->
                val section = config.getConfigurationSection(itemName) ?: return@forEach

                val itemStats = ItemStats(
                    displayName = section.getString("name", itemName) ?: itemName,
                    material = Material.valueOf(section.getString("material", "AIR")!!.uppercase()),
                    health = section.getDouble("health", 0.0),
                    attackPower = section.getDouble("attackPower", 0.0),
                    attackSpeed = section.getDouble("attackSpeed", 0.0),
                    defense = section.getDouble("defense", 0.0),
                    critChance = section.getDouble("critChance", 0.0),
                    critDamage = section.getDouble("critDamage", 0.0),
                    allowOffhand = section.getBoolean("allowOffhand", false),
                    lore = section.getStringList("lore")
                )

                itemsMap.getOrPut(categoryName) { mutableMapOf() }[itemName] = itemStats
            }


        }
    }

    fun GetItem(categoryName: String, itemName: String): ItemStats? {
        return itemsMap[categoryName]?.get(itemName)
    }

    fun CreateItem(category: String, itemName: String): ItemStack? {
        val itemStats = GetItem(category, itemName) ?: return null
        val item = ItemStack(itemStats.material)
        val meta = item.itemMeta ?: return null

        meta.setDisplayName(itemStats.displayName)
        meta.lore = itemStats.lore

        val pdc = meta.persistentDataContainer
        pdc.set(NamespacedKey(plugin, "category"), PersistentDataType.STRING, category)
        pdc.set(NamespacedKey(plugin, "itemname"), PersistentDataType.STRING, itemName)

        item.itemMeta = meta
        return item
    }

    fun GiveItem(categoryName: String, itemName: String, playerName: String?) {
        val player = plugin.server.getPlayer(playerName ?: return) ?: return
        val item = CreateItem(categoryName, itemName) ?: return
        player.inventory.addItem(item)
    }

    fun GetTotalStatValue(player: Player, statName: String): Double {
        var total = 0.0

        // Check armor slots
        player.inventory.armorContents.forEach { item ->
            total += getItemStatIfValid(item, statName, allowOffhandCheck = false)
        }

        // Check main-hand item
        total += getItemStatIfValid(player.inventory.itemInMainHand, statName, allowOffhandCheck = false)

        // Check offhand item with allowOffhand check
        total += getItemStatIfValid(player.inventory.itemInOffHand, statName, allowOffhandCheck = true)

        return total
    }

    // Helper function to retrieve stats from an item
    private fun getItemStatIfValid(item: ItemStack?, statName: String, allowOffhandCheck: Boolean): Double {
        if (item == null || item.type.isAir) return 0.0

        val meta = item.itemMeta ?: return 0.0
        val pdc = meta.persistentDataContainer

        val category = pdc.get(NamespacedKey(plugin, "category"), PersistentDataType.STRING) ?: return 0.0
        val itemName = pdc.get(NamespacedKey(plugin, "itemname"), PersistentDataType.STRING) ?: return 0.0
        val itemStats = GetItem(category, itemName) ?: return 0.0

        // If it's an offhand item, check if allowOffhand is set to true
        if (allowOffhandCheck) {
            val allowOffhand = pdc.get(NamespacedKey(plugin, "allowOffhand"), PersistentDataType.BOOLEAN) ?: false
            if (!allowOffhand) return 0.0
        }

        return when (statName) {
            "health" -> itemStats.health
            "attackPower" -> itemStats.attackPower
            "attackSpeed" -> itemStats.attackSpeed
            "defense" -> itemStats.defense
            "critChance" -> itemStats.critChance
            "critDamage" -> itemStats.critDamage
            else -> 0.0
        }
    }

    fun CreateDefaultFiles() {
        categoryList.forEach { categoryName ->
            val categoryFile = File(plugin.dataFolder, "items/$categoryName.yml")
            if (!categoryFile.exists()) {
                categoryFile.parentFile.mkdirs()  // Create the directory if it doesn't exist

                // Create a default item for the category
                val config = YamlConfiguration()
                config.set("Mighty_Sword.material", "WOODEN_SWORD")
                config.set("Mighty_Sword.name", "Mighty Sword")
                config.set("Mighty_Sword.health", 0.0)
                config.set("Mighty_Sword.attackPower", 10.0)
                config.set("Mighty_Sword.attackSpeed", 4.0)
                config.set("Mighty_Sword.defense", 0.0)
                config.set("Mighty_Sword.critChance", 0.0)
                config.set("Mighty_Sword.critDamage", 0.0)
                config.set("Mighty_Sword.allowOffhand", false)
                config.set("Mighty_Sword.lore", listOf("Wow", "Cool sword", "line n"))

                config.save(categoryFile)
            }
        }
    }
}

