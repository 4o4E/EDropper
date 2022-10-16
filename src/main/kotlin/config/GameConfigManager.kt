package top.e404.edropper.config

import com.charleskorn.kaml.Yaml
import com.sk89q.worldedit.extent.clipboard.Clipboard
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import top.e404.edropper.PL
import top.e404.edropper.game.ClipboardManager
import top.e404.eplugin.config.EMapConfig
import top.e404.eplugin.config.JarConfig
import top.e404.eplugin.util.execAsCommand
import top.e404.eplugin.util.selectByTo

/**
 * 游戏设置管理器
 */
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
 * @property group 游戏地图组
 * @property startLocation 游戏开始传送点的偏移
 * @property top 顶层地图
 * @property bottom 底层地图
 * @property buffer 缓冲区模板
 * @property bufferRepeat 一个缓冲区生成时模板的重复次数
 * @property target 目标方块, 碰到即成功
 * @property command 游戏对应阶段触发的指令
 */
@Serializable
data class GameConfig(
    val repeat: Boolean,
    val amount: Int,
    val group: MutableMap<String, MapGroupConfig>,
    @SerialName("start_location")
    val startLocation: StartLocation,
    val top: String,
    val bottom: String,
    val buffer: String,
    @SerialName("buffer_repeat")
    val bufferRepeat: Int,
    val target: MutableList<String>,
    val command: CommandConfig
) {
    val absolute by lazy {
        AbsolutelyGameConfig(
            repeat = repeat,
            amount = amount,
            startLocation = startLocation,
            group = group.entries.associate { (k, v) ->
                SchemGroupManager.getOrThrow(k) to v
            }.toMutableMap(),
            top = ClipboardManager.getOrThrow(top),
            bottom = ClipboardManager.getOrThrow(bottom),
            buffer = ClipboardManager.getOrThrow(buffer),
            bufferRepeat = bufferRepeat,
            target = target,
            command = command
        )
    }
}

/**
 * 实际游戏设置
 *
 * @property repeat 是否允许地图组重复
 * @property amount 地图组使用数量
 * @property group 地图组和对应的设置
 * @property startLocation 游戏开始传送点的偏移
 * @property top 游戏地图最上方的原理图
 * @property bottom 游戏地图最下方的原理图
 * @property buffer 缓冲区原理图
 * @property bufferRepeat 一个缓冲区生成时原理图的重复次数
 * @property target 目标方块, 碰到即成功
 * @property command 游戏对应阶段触发的指令
 */
data class AbsolutelyGameConfig(
    val repeat: Boolean,
    val amount: Int,
    val group: MutableMap<SchemGroup, MapGroupConfig>,
    val startLocation: StartLocation,
    val top: Clipboard,
    val bottom: Clipboard,
    val buffer: Clipboard,
    val bufferRepeat: Int,
    val target: MutableList<String>,
    val command: CommandConfig,
) {
    /**
     * 从配置中抽取
     *
     * @return 抽中的地图组
     */
    fun generator() = group.selectByTo(amount, repeat) { _, cfg -> cfg.weight }
}

/**
 * 地图组设置, 按照此设置生成地图的一段区域
 *
 * @property weight 权重
 * @property amount 选择的数量(从对应的组中选择指定数量的原理图)
 */
@Serializable
data class MapGroupConfig(
    val weight: Int,
    val amount: Int
)

/**
 * 指令设置
 *
 * @property enter 玩家进入游戏时执行的指令
 * @property start 游戏开始(玩家开始下落)时执行的指令
 * @property transition 游戏转场(玩家传送到下一场地)时执行的指令
 * @property fail 游戏失败(掉落在非指定方块上)时执行的指令
 * @property success 游戏成功(掉落在指定方块上)时执行的指令
 */
@Serializable
data class CommandConfig(
    val enter: MutableList<String>,
    val start: MutableList<String>,
    val transition: MutableList<String>,
    val fail: MutableList<String>,
    val success: MutableList<String>
) {
    fun onEnter(p: Player) = enter.exec(p)
    fun onStart(p: Player) = start.exec(p)
    fun onFail(p: Player) = fail.exec(p)
    fun onSuccess(p: Player) = success.exec(p)

    private fun List<String>.exec(p: Player) = forEach { cmd ->
        PlaceholderAPI.setPlaceholders(p, cmd).execAsCommand(p)
    }
}

@Serializable
data class StartLocation(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
)