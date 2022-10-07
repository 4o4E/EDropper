package top.e404.edropper.command

import top.e404.eplugin.command.ECommandManager
import top.e404.edropper.PL

object Commands : ECommandManager(
    PL,
    "edropper",
    Debug,
    Reload,
    Test
)