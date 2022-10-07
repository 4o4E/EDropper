package top.e404.edropper.command

import org.bukkit.command.CommandSender
import top.e404.edropper.PL
import top.e404.edropper.config.GameConfigManager
import top.e404.edropper.config.Lang
import top.e404.edropper.world.Game
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object Test : ECommand(
    PL,
    "test",
    "(?i)test",
    false,
    "edropper.admin"
) {
    override val usage: String
        get() = Lang["command.usage.test"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        val gameWorld = Game("test", GameConfigManager["example_game"]!!)
        gameWorld.create()
        gameWorld.generator()
        plugin.sendMsgWithPrefix(sender, "done")
    }
}