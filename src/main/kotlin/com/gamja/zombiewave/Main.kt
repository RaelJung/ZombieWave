package com.gamja.zombiewave

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    lateinit var gameManager: GameManager

    override fun onEnable() {
        saveDefaultConfig()
        gameManager = GameManager(this)
        getCommand("zw")?.setExecutor(Commands(this))
        server.pluginManager.registerEvents(EventListener(this), this)
        logger.info("ZombieWave 플러그인 시작!")
    }

    override fun onDisable() {
        logger.info("ZombieWave 플러그인 종료!")
    }
}