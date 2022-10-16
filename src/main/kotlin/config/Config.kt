package top.e404.edropper.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.e404.edropper.PL
import top.e404.eplugin.config.ESerializationConfig
import top.e404.eplugin.config.JarConfig
import top.e404.eplugin.config.RegexBwList

object Config : ESerializationConfig<ConfigData>(
    plugin = PL,
    path = "config.yml",
    default = JarConfig(PL, "config.yml"),
    serializer = ConfigData.serializer(),
    format = Yaml.default
) {
    var debug: Boolean
        get() = config.debug
        set(value) {
            config.debug = value
        }

    /**
     * 地图最低高度
     */
    val lowest: Int
        get() = config.lowest

    /**
     * 地图最高高度
     */
    val highest: Int
        get() = config.highest

    /**
     * 游戏区域半径
     */
    val radius: Int
        get() = config.radius

    /**
     * 游戏世界名
     */
    val world: String
        get() = config.world

    /**
     * 屏蔽指令设置
     */
    val preventCommand: RegexBwList
        get() = config.preventCommand
}

@Serializable
data class ConfigData(
    var debug: Boolean,
    val lowest: Int,
    val highest: Int,
    val radius: Int,
    val world: String,
    @SerialName("prevent_command")
    val preventCommand: RegexBwList
)