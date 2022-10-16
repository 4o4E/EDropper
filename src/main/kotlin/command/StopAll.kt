package top.e404.edropper.command

import org.bukkit.command.CommandSender
import top.e404.edropper.PL
import top.e404.edropper.config.Lang
import top.e404.edropper.game.GameManager
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object StopAll : ECommand(
    PL,
    "stopall",
    "(?i)stopall",
    false,
    "edropper.admin"
) {
    override val usage: String
        get() = Lang["command.usage.stopall"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        GameManager.shutdown()
    }
}