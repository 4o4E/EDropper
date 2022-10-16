package top.e404.edropper.config

import com.charleskorn.kaml.Yaml
import com.sk89q.worldedit.extent.clipboard.Clipboard
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import top.e404.edropper.PL
import top.e404.edropper.exception.NoSuchSchemGroupException
import top.e404.edropper.game.ClipboardManager
import top.e404.eplugin.config.EMapConfig
import top.e404.eplugin.config.JarConfig
import top.e404.eplugin.util.selectByTo

/**
 * 地图组管理器
 */
object SchemGroupManager : EMapConfig<String, SchemGroup>(
    plugin = PL,
    path = "games.yml",
    default = JarConfig(PL, "games.yml"),
    kSerializer = String.serializer(),
    vSerializer = SchemGroup.serializer(),
    format = Yaml.default,
    synchronized = false
) {
    fun getOrThrow(name: String) = this[name] ?: throw NoSuchSchemGroupException(name)
}

/**
 * 代表一个schem组
 *
 * @property repeat 是否允许重复
 * @property schematics schem名字和对应的权重
 */
@Serializable
data class SchemGroup(
    val repeat: Boolean,
    val schematics: MutableMap<String, Int>
) {
    val absolute by lazy {
        AbsolutelySchemGroup(
            repeat = repeat,
            schematics = schematics.entries.associate { (k, v) ->
                ClipboardManager.getOrThrow(k) to v
            }.toMutableMap(),
        )
    }
}

/**
 * 代表一个实际的schem组
 *
 * @property repeat 是否允许重复
 * @property schematics schem和对应的权重
 */
data class AbsolutelySchemGroup(
    val repeat: Boolean,
    val schematics: MutableMap<Clipboard, Int>
) {
    fun select(amount: Int) = schematics.selectByTo(amount, repeat) { _, v -> v }
}