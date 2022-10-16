package top.e404.edropper.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import top.e404.edropper.PL
import top.e404.edropper.game.GameManager
import top.e404.eplugin.listener.EListener

object GameListener : EListener(PL) {
    /**
     * 处理玩家移动
     */
    @EventHandler
    fun PlayerMoveEvent.onEvent() {
        GameManager[player]?.onMove(this)
    }

    /**
     * 玩家退出则游戏结束
     */
    @EventHandler
    fun PlayerQuitEvent.onEvent() {
        GameManager[player]?.stop()
    }

    /**
     * 阻止玩家点击
     */
    @EventHandler
    fun PlayerInteractEvent.onEvent() {
        if (GameManager[player] != null) isCancelled = true
    }

    /**
     * 阻止玩家使用指令
     */
    @EventHandler
    fun PlayerCommandPreprocessEvent.onEvent() {
        GameManager[player]?.onCommand(this)
    }

    @EventHandler
    fun EntityDamageEvent.onEvent() {
        val entity = entity
        if (entity is Player && GameManager[entity] != null) isCancelled = true
    }
}