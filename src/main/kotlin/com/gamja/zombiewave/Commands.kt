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
        if(sender !is Player) {
            sender.sendMessage("§c플레이어만 사용할 수 있습니다!")
            return true
        }

        if(args.isEmpty()) {
            sender.sendMessage("§e사용법: /zw join | /zw start")
            return true
        }

        when(args[0].lowercase()) {
            "join" -> plugin.gameManager.addPlayer(sender)
            "start" -> plugin.gameManager.addPlayer(sender)
            else -> sender.sendMessage("§c알 수 없는 명령어입니다.")
        }

        return true
    }
}