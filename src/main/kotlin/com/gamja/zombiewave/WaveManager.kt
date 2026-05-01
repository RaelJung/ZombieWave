package com.gamja.zombiewave

import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Zombie
import org.bukkit.scheduler.BukkitRunnable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

/*웨이브 시스템 관리*/
class WaveManager(private val plugin: Main) {

    var currentWave = 0
    private var waveTask: BukkitRunnable? = null
    private val maxWave = 5
    private val survivalTime = 30  // 마지막 웨이브 후 버텨야 할 시간 (초)

    fun startWave() {
        currentWave++
        val zombieCount = currentWave * 2  // 웨이브마다 좀비 3마리씩 증가

        // 1웨이브 이후부터 이전 웨이브 보상 지급
        if (currentWave > 1) {
            plugin.gameManager.players.forEach { player ->
                plugin.gameManager.rewardManager.giveReward(player, currentWave - 1)
            }
        }

        plugin.gameManager.players.forEach { player ->
            player.sendMessage(plugin.config.getString("messages.wave-start")
                ?.replace("{wave}", currentWave.toString()) ?: "웨이브 $currentWave 시작!")
            plugin.gameManager.scoreboardManager.update(player)
        }

        spawnZombies(zombieCount)

        // 마지막 웨이브면 생존 타이머 시작
        if (currentWave >= maxWave) {
            startSurvivalTimer()
            return
        }

        // 다음 웨이브 30초 후 자동 시작
        waveTask = object : BukkitRunnable() {
            override fun run() {
                if (plugin.gameManager.state == GameState.RUNNING) {
                    startWave()
                }
            }
        }
        waveTask?.runTaskLater(plugin, 20L * 30)  // 20틱 = 1초
    }

    //
    private fun startSurvivalTimer() {
        plugin.gameManager.players.forEach { player ->
            player.sendMessage(
                plugin.config.getString("messages.last-wave")
                    ?.replace("{time}", survivalTime.toString()) ?: "마지막 웨이브! ${survivalTime}초를 버텨라!"
            )
        }

        var remaining = survivalTime

        waveTask = object : BukkitRunnable() {
            override fun run() {
                if (plugin.gameManager.state != GameState.RUNNING) {
                    cancel()
                    return
                }

                remaining--

                // 10초, 5초, 3초, 2초, 1초 알림
                if (remaining in listOf(10, 5, 3, 2, 1)) {
                    plugin.gameManager.players.forEach { player ->
                        player.sendMessage(
                            plugin.config.getString("messages.survival-remaining")
                                ?.replace("{time}", remaining.toString()) ?: "${remaining}초 남았다!"
                        )
                    }
                }

                if (remaining <= 0) {
                    cancel()
                    plugin.gameManager.winGame()
                }
            }
        }
        waveTask?.runTaskTimer(plugin, 20L, 20L)
    }

    //좀비 구분 위해 붉은 이름표
    //[웨이브 N 좀비]
    private fun spawnZombies(count: Int) {
        val players = plugin.gameManager.players
        if(players.isEmpty()) return    //다 죽었으면 소환 안 되도록

        //현재 월드에 좀비 너무 많으면 스폰 중단
        val world = players[0].world
        val existingZombies = world.entities.count { it is org.bukkit.entity.Zombie && it.customName() != null }
        if (existingZombies > 50) return    //최대 49

        //forEach 중첩 제거 -> count마리만 스폰 되도록!
        repeat(count) { index ->
            val player = players[index % players.size]
            val loc = getRandomLocation(player.location)
            val zombie = player.world.spawnEntity(loc, EntityType.ZOMBIE)
            zombie.customName(Component.text("[웨이브 $currentWave] 좀비", NamedTextColor.RED))
            zombie.isCustomNameVisible = true
        }
    }

    private fun getRandomLocation(center: Location): Location {
        val random = java.util.Random()
        val range = 10.0  // 플레이어 주변 10블록 이내
        val x = center.x + (random.nextDouble() * range * 2 - range)
        val z = center.z + (random.nextDouble() * range * 2 - range)
        return Location(center.world, x, center.y, z)
    }

    fun stopWave() {
        waveTask?.cancel()
        waveTask = null
        currentWave = 0

        //웨이브 종료 후, 월드에 남아있는 플러그인 소환 좀비 전부 제거
        plugin.gameManager.players.firstOrNull()?.world?.entities
            ?.filter { it is Zombie && it.customName() != null }
            ?.forEach { it.remove() }
    }
}