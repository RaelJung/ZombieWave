package com.gamja.zombiewave

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.time.Duration

//상태 관리
enum class GameState{
    WAITING,    //대기 중
    RUNNING,    //진행 중
    ENDING      //종료 중
}

/*게임 상태 관리*/
class GameManager(private val plugin: Main) {
    var state: GameState = GameState.WAITING
    val players: MutableList<Player> = mutableListOf()
    val waveManager = WaveManager(plugin)
    val playerManager = PlayerManager()
    val scoreboardManager = ScoreboardManager(plugin)
    val rewardManager = RewardManager(plugin)

    fun startGame(){
        //이미 진행중이면 새로 시작 X
        if(state != GameState.WAITING) return

        state = GameState.RUNNING

        var count = 3

        object : BukkitRunnable() {
            override fun run() {
                //게임 시작 카운트 다운
                if (count > 0) {
                    players.forEach { player ->
                        player.sendMessage(
                            plugin.config.getString("messages.countdown")
                                ?.replace("{time}", count.toString()) ?: "${count}초!"
                        )
                    }
                    count--
                } else {
                    cancel()
                    players.forEach { player ->
                        player.gameMode = GameMode.SURVIVAL
                        player.health = 20.0
                        player.foodLevel = 20
                        player.sendMessage(plugin.config.getString("messages.game-start") ?: "게임 시작!")
                        player.sendMessage(plugin.config.getString("messages.countdown-go") ?: "GO!")
                        showTitle(player, "title-game-start", "title-game-start-sub")
                        scoreboardManager.update(player)
                    }
                    waveManager.startWave()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L)  //게임 시작하면 웨이브 시작
    }

    fun endGame(){
        state = GameState.ENDING
        waveManager.stopWave()  //웨이브 중단
        playerManager.reset()
        clearZombies()  //맵에서 좀비 클리어
        players.forEach { player ->
            player.sendMessage(plugin.config.getString("messages.game-end") ?: "게임 종료!")
            showTitle(player, "title-game-over", "title-game-over-sub")
            player.gameMode = GameMode.ADVENTURE
            scoreboardManager.clear(player)
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

    fun winGame() {
        state = GameState.ENDING
        waveManager.stopWave()
        clearZombies()  //맵에서 좀비 클리어
        players.forEach { player ->
            player.sendMessage(plugin.config.getString("messages.win") ?: "클리어!")
            showTitle(player, "title-win", "title-win-sub")
            player.gameMode = GameMode.ADVENTURE
            scoreboardManager.clear(player)
        }
        state = GameState.WAITING
        players.clear()
    }

    //맵에서 소환된 좀비 제거
    private fun clearZombies() {
        players.firstOrNull()?.world?.entities
            ?.filter { it is org.bukkit.entity.Zombie && it.customName() != null }  //웨이브로 생성된 좀비만 지우도록
            ?.forEach { it.remove() }
    }

    //WaveManager.kt에 사용하기 때문에 internal
    internal fun showTitle(player: Player, titleKey: String, subtitleKey: String, vararg placeholders: Pair<String, String>) {
        var titleText = plugin.config.getString("messages.$titleKey") ?: ""
        var subText = plugin.config.getString("messages.$subtitleKey") ?: ""
        placeholders.forEach { (key, value) ->
            titleText = titleText.replace("{$key}", value)
            subText = subText.replace("{$key}", value)
        }
        val title = Title.title(
            Component.text(titleText),
            Component.text(subText),
            Title.Times.times(
                Duration.ofMillis(500),    //페이드 인
                Duration.ofSeconds(2),      //유지
                Duration.ofMillis(500)  //페이드 아웃
            )
        )
        player.showTitle(title)
    }
}