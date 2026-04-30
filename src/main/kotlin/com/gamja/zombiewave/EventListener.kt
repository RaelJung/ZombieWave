package com.gamja.zombiewave

import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent

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

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        if (plugin.gameManager.state != GameState.RUNNING) return
        if (!plugin.gameManager.players.contains(player)) return

        plugin.gameManager.players.remove(player)
        plugin.gameManager.players.forEach {
            it.sendMessage(
                plugin.config.getString("messages.player-death")
                    ?.replace("{player}", player.name)
                    ?.replace("{count}", plugin.gameManager.players.size.toString())
                    ?: "${player.name} 사망!"
            )
        }

        if (plugin.gameManager.players.isEmpty()) {
            plugin.gameManager.players.forEach {
                it.sendMessage(plugin.config.getString("messages.game-over") ?: "게임 오버!")
            }
            plugin.gameManager.endGame()
        }
    }
}