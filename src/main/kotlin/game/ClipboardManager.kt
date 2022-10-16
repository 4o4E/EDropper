package top.e404.edropper.game

import top.e404.edropper.PL
import top.e404.edropper.exception.NoSuchSchemException
import top.e404.eplugin.hook.worldedit.EClipboardManager

object ClipboardManager : EClipboardManager(
    plugin = PL,
    folder = PL.dataFolder.resolve("schem")
) {
    fun getOrThrow(name: String) = this[name] ?: throw NoSuchSchemException(name)
}