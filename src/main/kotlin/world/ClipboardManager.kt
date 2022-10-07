package top.e404.edropper.world

import top.e404.edropper.PL
import top.e404.edropper.config.NoSuchSchemException
import top.e404.eplugin.hook.worldedit.EClipboardManager

object ClipboardManager : EClipboardManager(
    plugin = PL,
    folder = PL.dataFolder.resolve("schem")
) {
    fun getOrThrow(name: String) = this[name] ?: throw NoSuchSchemException(name)
}