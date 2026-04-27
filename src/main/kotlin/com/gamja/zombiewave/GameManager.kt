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
            player.sendMessage("§a게임 시작! 좀비를 막으세요 :)")
        }
        plugin.logger.info("게임 시작! 플레이어 수: ${players.size}")
    }

    fun endGame(){
        state = GameState.ENDING
        players.forEach { player ->
            player.sendMessage("§c게임 종료!")
            player.gameMode = GameMode.ADVENTURE
        }
        state = GameState.WAITING
        players.clear()
    }

    fun addPlayer(player: Player) {
        if(state != GameState.WAITING){
            player.sendMessage("§c게임이 이미 진행 중입니다!")
            return
        }
        players.add(player)
        player.sendMessage("§e게임에 참가했습니다! 현재 ${players.size}명")
    }
}