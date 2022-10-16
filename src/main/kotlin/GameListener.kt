package top.e404.edropper

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import top.e404.edropper.game.GameManager
import top.e404.eplugin.listener.EListener

object GameListener : EListener(PL) {
    @EventHandler
    fun PlayerMoveEvent.onEvent() {
        GameManager[player]?.onMove(this)
    }

    @EventHandler
    fun PlayerQuitEvent.onEvent() {
        GameManager[player]?.stop()
    }
}