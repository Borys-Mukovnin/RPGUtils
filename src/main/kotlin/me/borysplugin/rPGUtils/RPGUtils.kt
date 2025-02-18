package me.borysplugin.rPGUtils


import MySQLManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.net.URI
import java.nio.file.*

class RPGUtils : JavaPlugin() {

    val itemsManager = ItemsManager(this)
    val mySQLManager = MySQLManager("localhost","3306","sys","root","ENmPU5;k7586E1Aa")
    val playerDataManager = PlayerDataManager(this,mySQLManager)
    val playerManager = PlayerManager(this,playerDataManager,itemsManager)
    val combatManager = CombatManager(this,playerManager)
    val eventListeners = EventListeners(this,playerManager,combatManager)
    val itemsCommands = ItemsCommands(this,itemsManager)
    val utilsCommands = UtilsCommands()

    override fun onEnable() {

        mySQLManager.connect()

        itemsManager.CreateDefaultFiles()
        itemsManager.LoadItems()
        combatManager.startActionBarUpdates()

        server.pluginManager.registerEvents(eventListeners,this)

        getCommand("items")?.setExecutor(itemsCommands)
        getCommand("utils")?.setExecutor(utilsCommands)

    }


    override fun onDisable() {
        // Plugin shutdown logic
        mySQLManager.disconnect()
    }

}
