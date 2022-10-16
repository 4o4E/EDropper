package top.e404.edropper.command

import top.e404.edropper.PL
import top.e404.eplugin.command.ECommandManager

object Commands : ECommandManager(
    PL,
    "edropper",
    Debug,
    Reload,
    Test,
    StopAll
)