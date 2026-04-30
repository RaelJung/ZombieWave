package com.gamja.zombiewave

import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitRunnable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

/*웨이브 시스템 관리*/
class WaveManager(private val plugin: Main) {

    var currentWave = 0
    private var waveTask: BukkitRunnable? = null

    fun startWave() {
        currentWave++
        val zombieCount = currentWave * 3  // 웨이브마다 좀비 3마리씩 증가

        plugin.gameManager.players.forEach { player ->
            player.sendMessage(plugin.config.getString("messages.wave-start")
                ?.replace("{wave}", currentWave.toString()) ?: "웨이브 $currentWave 시작!")
            plugin.gameManager.scoreboardManager.update(player)
        }

        spawnZombies(zombieCount)

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

    //좀비 구분 위해 붉은 이름표
    //[웨이브 N 좀비]
    private fun spawnZombies(count: Int) {
        repeat(count) {
            plugin.gameManager.players.forEach { player ->
                val loc = getRandomLocation(player.location)
                val zombie = player.world.spawnEntity(loc, EntityType.ZOMBIE)
                zombie.customName(Component.text("[웨이브 $currentWave] 좀비", NamedTextColor.RED))
                zombie.isCustomNameVisible = true
            }
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
    }
}