package com.gamja.zombiewave

import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

/*킬 감지 이벤트 관리(스코어보드 용)*/
class EventListener(private val plugin: Main) : Listener {

    @EventHandler
    fun onZombieDeath(event: EntityDeathEvent) {
        if (event.entity !is Zombie) return
        val killer = event.entity.killer ?: return
        if (killer !is Player) return
        if (plugin.gameManager.state != GameState.RUNNING) return

        plugin.gameManager.playerManager.addKill(killer)
        plugin.gameManager.scoreboardManager.update(killer)
    }
}