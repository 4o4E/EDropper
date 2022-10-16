package top.e404.edropper.command

import org.bukkit.command.CommandSender
import top.e404.edropper.PL
import top.e404.edropper.config.Config
import top.e404.edropper.config.Lang
import top.e404.edropper.game.GameLocation
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

        val l = GameLocation(0, 0)
        sender.sendMessage("""x: ${l.fromX}..${l.toX}
            |y: ${Config.lowest}..${Config.highest}
            |z: ${l.fromZ}..${l.toZ}
        """.trimMargin())
        l.clear()
    }
}