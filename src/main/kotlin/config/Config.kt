package top.e404.edropper.config

import top.e404.edropper.PL
import top.e404.eplugin.config.EConfig
import top.e404.eplugin.config.JarConfig

object Config : EConfig(
    plugin = PL,
    path = "config.yml",
    default = JarConfig(PL, "config.yml")
) {
    var debug: Boolean
        get() = config.getBoolean("debug")
        set(value) {
            config.set("debug", value)
        }

    /**
     * 地图最低高度
     */
    val lowest by lazy { config.getInt("lowest") }

    /**
     * 地图最高高度
     */
    val highest by lazy { config.getInt("highest") }

    /**
     * 游戏区域半径
     */
    val radius by lazy { config.getInt("radius") }

    /**
     * 游戏世界名
     */
    val world: String
        get() = config.getString("game") ?: "game"
}