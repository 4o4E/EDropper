package top.e404.edropper.hook

import top.e404.edropper.PL
import top.e404.eplugin.hook.EHookManager

object HookManager : EHookManager(
    plugin = PL,
    WeHook,
)