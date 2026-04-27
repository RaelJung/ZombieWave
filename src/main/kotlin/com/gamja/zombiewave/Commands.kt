package com.gamja.zombiewave

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(private val plugin: Main) : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if(sender !is Player){
            sender.sendMessage(plugin.config.getString("messages.player-only") ?: "플레이어만 가능!")
            return true
        }

        if(args.isEmpty()){
            sender.sendMessage(plugin.config.getString("messages.usage") ?: "사용법 없음")
            return true
        }

        when(args[0].lowercase()){
            "join" -> plugin.gameManager.addPlayer(sender)
            "start" -> plugin.gameManager.startGame()
            else -> sender.sendMessage(plugin.config.getString("messages.unknown-command") ?: "알 수 없는 명령어!")
        }

        return true
    }
}