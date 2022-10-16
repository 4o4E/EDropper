package top.e404.edropper

import top.e404.edropper.command.Commands
import top.e404.edropper.config.Config
import top.e404.edropper.config.GameConfigManager
import top.e404.edropper.config.Lang
import top.e404.edropper.config.SchemGroupManager
import top.e404.edropper.game.ClipboardManager
import top.e404.edropper.game.GameManager
import top.e404.edropper.hook.HookManager
import top.e404.edropper.listener.GameListener
import top.e404.eplugin.EPlugin

class EDropper : EPlugin() {
    override val debugPrefix: String
        get() = langManager.getOrElse("debug_prefix") { "&7[&bEDropperDebug&7]" }
    override val prefix: String
        get() = langManager.getOrElse("prefix") { "&7[&6EDropper&7]" }

    override var debug: Boolean
        get() = Config.debug
        set(value) {
            Config.debug = value
        }
    override val langManager by lazy { Lang }

    override fun onEnable() {
        PL = this
        Config.load(null)
        Lang.load(null)
        GameConfigManager.load(null)
        SchemGroupManager.load(null)
        ClipboardManager.load()
        GameListener.register()
        Commands.register()
        HookManager.register()
        GameManager.initWorld()
        GameManager.startTick()
        info("&a加载完成")
    }

    override fun onDisable() {
        GameManager.shutdown()
        cancelAllTask()
        info("&a卸载完成")
    }
}

lateinit var PL: EDropper
    private set