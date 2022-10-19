package top.e404.edropper.game

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.extent.clipboard.Clipboard
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import top.e404.edropper.PL
import top.e404.edropper.config.Config
import top.e404.edropper.config.GameConfig
import top.e404.edropper.config.Lang
import top.e404.eplugin.hook.worldedit.pasteCenter
import top.e404.eplugin.util.getBlockUnderFoot
import top.e404.eplugin.util.select

/**
 * 代表一场游戏
 *
 * @property p 参加游戏的玩家
 * @property location 玩家进入游戏前的位置
 * @param gameConfig 对应的游戏设置
 */
class Game private constructor(
    val p: Player,
    gameConfig: GameConfig
) {
    companion object {
        @JvmStatic
        fun create(p: Player, config: GameConfig): Game {
            GameManager[p]?.let { return it }
            return Game(p, config).also {
                GameManager.games.add(it)
            }
        }
    }

    val location = p.location
    var state = GameState.INITIALIZATION

    /**
     * 游戏占用的物理区域
     */
    val locations = mutableListOf<GameLocation>()

    /**
     * 当前游戏中的游戏区域
     */
    var currentlyLocation = 0

    /**
     * 申请一块区域
     */
    fun applyLocation() = GameManager.getAndUse(this) {
        //it.clear()
    }

    /**
     * 游戏设置
     */
    val config = gameConfig.absolute

    /**
     * 生成地图所使用的原理图列表, 通过设置生成游戏对应的原理图列表
     */
    val list by lazy {
        ArrayList<Clipboard>().also { list ->
            // 最上方
            list.add(config.top)
            // 缓冲区
            repeat(config.bufferRepeat) { list.add(config.buffer) }
            config.group.select(config.amount, config.repeat) { _, v ->
                v.weight
            }.forEach { (schem, mapGroupConfig) ->
                // 段
                list.addAll(schem.absolute.select(mapGroupConfig.amount))
                // 缓冲区
                repeat(config.bufferRepeat) { list.add(config.buffer) }
            }
            // 最下方
            list.add(config.bottom)
        }
    }


    var startY: Int = 0

    lateinit var startLocation: Location

    /**
     * 准备游戏地图
     */
    fun complete() {
        var y = Config.highest
        var location = applyLocation()
        val l = config.startLocation
        startY = (Config.highest - list.first().dimensions.y + l.y).toInt()
        startLocation = Location(
            GameManager.world,
            location.centerX + l.x,
            Config.highest - list.first().dimensions.y + l.y,
            location.centerZ + l.z,
            l.yaw,
            l.pitch
        )
        WorldEdit.getInstance().newEditSession(GameManager.weWorld).use { session ->
            list.forEach { schem ->
                schem.apply {
                    y -= dimensions.y
                    if (y < Config.lowest) {
                        location = applyLocation()
                        y = Config.highest - dimensions.y
                    }
                    pasteCenter(session, location.centerX, y, location.centerZ)
                }
            }
        }
    }

    /**
     * 玩家入场
     */
    fun start() {
        p.teleport(startLocation)
        config.command.onEnter(p)
        state = GameState.PREPARE
    }

    /**
     * 游戏每tick计算
     */
    fun onTick() {
        if (state != GameState.GAMING) return
        // 落地检测
        val blocks = p.getBlockUnderFoot()
        PL.debug { "blocks:\n${blocks.joinToString("\n") { "  ${it.type.name} x: ${it.x}, y: ${it.y} z: ${it.z}" }}" }
        if (blocks.all { it.isEmpty }) return
        if (blocks.any { it.type.name in config.target }) {
            config.command.onSuccess(p)
            stop()
            return
        }
        config.command.onFail(p)
        stop()
    }

    /**
     * 玩家移动时触发
     */
    fun onMove(event: PlayerMoveEvent) {
        if (!state.listen) return
        val to = event.to ?: return
        // 阻止跨世界传送
        if (event is PlayerTeleportEvent && event.to!!.world != event.from.world) {
            PL.debug { "阻止跨世界传送" }
            event.isCancelled = true
            PL.sendMsgWithPrefix(p, Lang["game.prevent_teleport"])
            return
        }
        // 开始下落
        if (state == GameState.PREPARE) {
            if (to.y < startY) {
                PL.debug { "开始下落" }
                state = GameState.GAMING
                config.command.onStart(p)
            }
            return
        }
        // 转场
        if (to.y < Config.lowest) {
            PL.debug { "转场" }
            println(to)
            var current = locations[currentlyLocation]
            println(current)
            val l = to.add(
                -current.centerX.toDouble(),
                0.0,
                -current.centerZ.toDouble(),
            )
            current = locations[++currentlyLocation]
            println(current)
            l.add(
                current.centerX.toDouble(),
                0.0,
                current.centerZ.toDouble(),
            )
            l.y = Config.highest.toDouble()
            println(l)
            p.teleport(l)
            // 缓降
            p.addPotionEffect(
                PotionEffect(
                    PotionEffectType.SLOW_FALLING,
                    20 * 3,
                    0,
                    false,
                    false
                )
            )
            return
        }
    }

    fun onCommand(event: PlayerCommandPreprocessEvent) {
        if (!p.hasPermission("edropper.bypass.command")
            && Config.preventCommand.matches(event.message)
        ) event.isCancelled = true
    }

    /**
     * 游戏结束
     */
    fun stop() {
        state = GameState.FINISH
        GameManager.games.remove(this)
        p.teleport(location)
        clear {}
    }

    fun clear(afterClear: () -> Unit) {
        PL.runTaskAsync {
            locations.forEach(GameLocation::clear)
            PL.runTask(afterClear)
        }
    }
}