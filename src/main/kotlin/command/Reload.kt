package top.e404.edropper.command

import org.bukkit.command.CommandSender
import top.e404.edropper.PL
import top.e404.edropper.config.Config
import top.e404.edropper.config.GameConfigManager
import top.e404.edropper.config.Lang
import top.e404.edropper.config.SchemGroupManager
import top.e404.edropper.game.ClipboardManager
import top.e404.edropper.hook.HookManager
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object Reload : ECommand(
    PL,
    "reload",
    "(?i)reload|r",
    false,
    "edropper.admin"
) {
    override val usage: String
        get() = Lang["command.usage.reload"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        plugin.runTaskAsync {
            Config.load(null)
            Lang.load(null)
            GameConfigManager.load(null)
            SchemGroupManager.load(null)
            ClipboardManager.load()
            HookManager.register()
            plugin.sendMsgWithPrefix(sender, Lang["command.reload_done"])
        }
    }
}