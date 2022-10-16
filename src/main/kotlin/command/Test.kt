package top.e404.edropper.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.edropper.PL
import top.e404.edropper.config.GameConfigManager
import top.e404.edropper.config.Lang
import top.e404.edropper.game.Game
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
        if (sender !is Player) {
            plugin.sendMsgWithPrefix(sender, Lang["message.non_player"])
            return
        }
        val game = Game.create(sender, GameConfigManager["example_game"]!!)
        game.start()
        plugin.sendMsgWithPrefix(sender, "done")
    }
}