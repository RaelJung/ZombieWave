package com.gamja.zombiewave

import org.bukkit.GameMode
import org.bukkit.entity.Player

//상태 관리
enum class GameState{
    WAITING,    //대기 중
    RUNNING,    //진행 중
    ENDING      //종료 중
}

class GameManager(private val plugin: Main) {
    var state: GameState = GameState.WAITING
    val players: MutableList<Player> = mutableListOf()

    fun startGame(){
        //이미 진행중이면 새로 시작 X
        if(state != GameState.WAITING){
            return
        }

        state = GameState.RUNNING
        players.forEach { player ->
            player.gameMode = GameMode.SURVIVAL
            player.health = player.maxHealth
            player.foodLevel = 20
            player.sendMessage(plugin.config.getString("messages.game-start") ?: "게임 시작!")
        }
    }

    fun endGame(){
        state = GameState.ENDING
        players.forEach { player ->
            player.sendMessage(plugin.config.getString("messages.game-end") ?: "게임 종료!")
            player.gameMode = GameMode.ADVENTURE
        }
        state = GameState.WAITING
        players.clear()
    }

    fun addPlayer(player: Player) {
        if(state != GameState.WAITING){
            player.sendMessage(plugin.config.getString("messages.already-running") ?: "이미 진행 중!")
            return
        }
        players.add(player)
        val msg = (plugin.config.getString("messages.join") ?: "참가! {count}명")
            .replace("{count}", players.size.toString())
        player.sendMessage(msg)
    }
}