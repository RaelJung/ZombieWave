package com.gamja.zombiewave

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

/*웨이브 클리어마다 보상 제공 관리*/
class RewardManager(private val plugin: Main) {

    fun giveReward(player: Player, wave: Int) {
        val items = getRewardItems(wave)
        items.forEach { item ->
            player.inventory.addItem(item)
        }
        player.sendMessage(
            plugin.config.getString("messages.wave-clear")
                ?.replace("{wave}", wave.toString()) ?: "웨이브 $wave 클리어!"
        )
    }

    private fun getRewardItems(wave: Int): List<ItemStack> {
        return when (wave) {
            1 -> listOf(
                createItem(Material.IRON_SWORD, "§f철 검", 1)
            )
            2 -> listOf(
                createItem(Material.BOW, "§f활", 1),
                createItem(Material.ARROW, "§f화살", 16)
            )
            3 -> listOf(
                createItem(Material.IRON_CHESTPLATE, "§f철 흉갑", 1),
                createItem(Material.COOKED_BEEF, "§f스테이크", 8)
            )
            4 -> listOf(
                createItem(Material.DIAMOND_SWORD, "§b다이아몬드 검", 1),
                createItem(Material.GOLDEN_APPLE, "§6황금 사과", 2)
            )
            5 -> listOf(
                createItem(Material.DIAMOND_CHESTPLATE, "§b다이아몬드 흉갑", 1),
                createItem(Material.ENCHANTED_GOLDEN_APPLE, "§d인챈트 황금 사과", 1)
            )
            else -> listOf(
                createItem(Material.GOLDEN_APPLE, "§6황금 사과", 1)
            )
        }
    }

    private fun createItem(material: Material, name: String, amount: Int): ItemStack {
        val item = ItemStack(material, amount)
        val meta = item.itemMeta
        meta.displayName(Component.text(name, NamedTextColor.WHITE))
        item.itemMeta = meta
        return item
    }
}