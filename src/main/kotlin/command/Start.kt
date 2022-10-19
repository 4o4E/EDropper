package top.e404.edropper.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import top.e404.edropper.PL
import top.e404.edropper.config.GameConfigManager
import top.e404.edropper.config.Lang
import top.e404.edropper.game.Game
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object Start : ECommand(
    PL,
    "start",
    "(?i)start",
    false,
    "edropper.admin"
) {
    override val usage: String
        get() = Lang["command.usage.start"].color()

    override fun onTabComplete(
        sender: CommandSender,
        args: Array<out String>,
        complete: MutableList<String>
    ) {
        when (args.size) {
            2 -> complete.addOnlinePlayers()
            3 -> complete.addAll(GameConfigManager.config.keys)
        }
    }

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (args.size != 3) {
            plugin.sendMsgWithPrefix(sender, usage)
            return
        }
        val (_, playerName, configName) = args
        val player = Bukkit.getPlayer(playerName)
        if (player == null) {
            plugin.sendMsgWithPrefix(sender, Lang["command.invalid_player"])
            return
        }
        val config = GameConfigManager[configName]
        if (config == null) {
            plugin.sendMsgWithPrefix(
                sender,
                Lang["command.invalid_game_config", "game" to configName]
            )
            return
        }
        val game = Game.create(player, config)
        plugin.runTaskAsync {
            game.complete()
            plugin.runTask {
                game.start()
                plugin.sendMsgWithPrefix(sender, Lang["command.start_game"])
            }
        }
    }
}