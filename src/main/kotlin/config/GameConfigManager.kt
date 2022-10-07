package top.e404.edropper.config

import com.charleskorn.kaml.Yaml
import com.sk89q.worldedit.extent.clipboard.Clipboard
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import top.e404.edropper.PL
import top.e404.edropper.world.ClipboardManager
import top.e404.eplugin.config.EMapConfig
import top.e404.eplugin.config.JarConfig

object GameConfigManager : EMapConfig<String, GameConfig>(
    plugin = PL,
    path = "games.yml",
    default = JarConfig(PL, "games.yml"),
    kSerializer = String.serializer(),
    vSerializer = GameConfig.serializer(),
    format = Yaml.default,
    synchronized = false
)

/**
 * 游戏设置
 *
 * @property repeat 是否允许重复
 * @property amount 游戏使用的地图数
 * @property top 顶层地图
 * @property bottom 底层地图
 * @property group 游戏地图组
 * @property score 通关分值
 */
@Serializable
data class GameConfig(
    val repeat: Boolean,
    val amount: Int,
    val top: String,
    val bottom: String,
    val group: MutableMap<String, Int>,
    val score: Int
) {
    val absolute by lazy {
        AbsolutelyGameConfig(
            repeat = repeat,
            amount = amount,
            top = ClipboardManager.getOrThrow(top),
            bottom = ClipboardManager.getOrThrow(bottom),
            group = group.entries.associate { (k, v) ->
                ClipboardManager.getOrThrow(k) to v
            }.toMutableMap(),
            score = score
        )
    }
}

class NoSuchSchemException(
    name: String
) : Exception("cannot find schem file with name($name)")

data class AbsolutelyGameConfig(
    val repeat: Boolean,
    val amount: Int,
    val top: Clipboard,
    val bottom: Clipboard,
    val group: MutableMap<Clipboard, Int>,
    val score: Int
)