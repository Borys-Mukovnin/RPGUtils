package me.borysplugin.rPGUtils

import me.borysplugin.rPGUtils.commands.ItemsCommands
import me.borysplugin.rPGUtils.commands.OpenMenuCommand
import me.borysplugin.rPGUtils.commands.UtilsCommands
import me.borysplugin.rPGUtils.events.CombatListener
import me.borysplugin.rPGUtils.events.StatManagerListeners
import me.borysplugin.rPGUtils.managers.CombatManager
import me.borysplugin.rPGUtils.managers.ItemsManager
import me.borysplugin.rPGUtils.managers.PlayerDataManager
import me.borysplugin.rPGUtils.managers.PlayerManager
import me.borysplugin.rPGUtils.storage.DatabaseManager
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class RPGUtils : JavaPlugin() {

    private lateinit var configFile: File
    private lateinit var config: FileConfiguration


    // <------------------- MANAGERS --------------------->

    val itemsManager = ItemsManager(this)
    val mySQLManager = DatabaseManager("localhost", "3306", "sys", "root", "ENmPU5;k7586E1Aa")
    val playerDataManager = PlayerDataManager(this,mySQLManager)
    val playerManager = PlayerManager(this,playerDataManager,itemsManager)
    val combatManager = CombatManager(this,playerManager)

    // <------------------- GUI --------------------->



    // <------------------- LISTENERS --------------------->

    val statManagerListeners = StatManagerListeners(this,playerManager)
    val combatListener = CombatListener(this,playerManager,combatManager)


    // <------------------- COMMANDS --------------------->

    val itemsCommands = ItemsCommands(this, itemsManager)
    val utilsCommands = UtilsCommands()

    // <--------------------------------------------------->

    override fun onEnable() {

        mySQLManager.connect()

        itemsManager.CreateDefaultFiles()
        itemsManager.LoadItems()

        combatManager.startActionBarUpdates()

        server.pluginManager.registerEvents(statManagerListeners,this)
        server.pluginManager.registerEvents(combatListener,this)


        getCommand("items")?.setExecutor(itemsCommands)
        getCommand("utils")?.setExecutor(utilsCommands)
        getCommand("openmenu")?.setExecutor(OpenMenuCommand(this))

    }

    override fun saveDefaultConfig() {
        configFile = File(dataFolder, "config.yml")

        if (!configFile.exists()) {
            saveResource("config.yml", false) // Copies default config.yml from resources
            logger.info("Default config.yml created.")
        }
    }

    private fun loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile)
    }

    fun getConfigValue(fieldName: String, moduleName: String? = null): Any? {
        return if (moduleName != null) {
            // Read from a specific module
            config.get("modules.$moduleName.$fieldName")
        } else {
            // Read a general property
            config.get(fieldName)
        }
    }



    override fun onDisable() {
        // Plugin shutdown logic
        mySQLManager.disconnect()
    }
    }