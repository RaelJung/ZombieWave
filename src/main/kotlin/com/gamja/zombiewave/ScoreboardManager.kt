package com.gamja.zombiewave

import io.papermc.paper.scoreboard.numbers.NumberFormat
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.RenderType

/*사이드바 스코어보드 관리*/
class ScoreboardManager(private val plugin: Main) {

    fun update(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().newScoreboard
        val objective: Objective = scoreboard.registerNewObjective(
            "zombiewave", "dummy",
            Component.text("§6=== ZombieWave ==="),
            RenderType.INTEGER
        )
        objective.displaySlot = DisplaySlot.SIDEBAR

        val wave = plugin.gameManager.waveManager.currentWave
        val kills = plugin.gameManager.playerManager.getKills(player)

        val waveScore = objective.getScore("§f웨이브: $wave")
        waveScore.score = 2 //순서 지정(내림차순)
        waveScore.numberFormat(NumberFormat.blank())

        val killScore = objective.getScore("§f킬 수: $kills")
        killScore.score = 1 //순서 지정
        killScore.numberFormat(NumberFormat.blank())

        player.scoreboard = scoreboard
    }


    fun clear(player: Player) {
        player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    }
}