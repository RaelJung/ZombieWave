package com.gamja.zombiewave

import org.bukkit.entity.Player

/*플레이어 킬 수 관리*/
class PlayerManager {
    private val killCount: MutableMap<Player, Int> = mutableMapOf()

    fun addKill(player: Player) {
        killCount[player] = (killCount[player] ?: 0) + 1
    }

    fun getKills(player: Player): Int {
        return killCount[player] ?: 0
    }

    fun reset() {
        killCount.clear()
    }
}