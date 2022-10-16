package top.e404.edropper.game

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.extent.clipboard.Clipboard
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import top.e404.edropper.PL
import top.e404.edropper.config.Config
import top.e404.edropper.config.GameConfig
import top.e404.edropper.config.Lang
import top.e404.eplugin.EPlugin.Companion.formatAsConst
import top.e404.eplugin.hook.worldedit.pasteCenter

/**
 * 代表一场游戏
 *
 * @property p 参加游戏的玩家
 * @property location 玩家进入游戏前的位置
 * @param config 对应的游戏设置
 */
class Game(
    val p: Player,
    val location: Location,
    config: GameConfig
) {
    var state = GameState.INITIALIZATION

    /**
     * 游戏占用的物理区域
     */
    val locations = mutableSetOf<GameLocation>()

    /**
     * 申请一块区域并清空
     */
    fun applyLocation() = GameManager.getAndUse(this) {
        it.clear()
    }

    /**
     * 游戏设置
     */
    val cfg = config.absolute

    /**
     * 生成地图所使用的原理图列表, 通过设置生成游戏对应的原理图列表
     */
    val list by lazy {
        ArrayList<Clipboard>().also { list ->
            // 最上方
            list.add(cfg.top)
            // 缓冲区
            repeat(cfg.bufferRepeat) { list.add(cfg.buffer) }
            cfg.group.forEach { (schem, cfg) ->
                // 段
                list.addAll(schem.absolute.select(cfg.amount))
                // 缓冲区
                repeat(this.cfg.bufferRepeat) { list.add(this.cfg.buffer) }
            }
            // 最下方
            list.add(cfg.bottom)
        }
    }


    var startY: Int = 0

    /**
     * 准备游戏地图
     */
    fun initialize() {
        var y = Config.highest
        var location = applyLocation()
        val l = cfg.startLocation
        startY = (Config.highest - list.first().dimensions.y + l.y).toInt()
        val startLocation = Location(
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
        p.teleport(startLocation)
        cfg.command.onStart(p)
        state = GameState.PREPARE
    }

    /**
     * 玩家移动时触发
     */
    fun onMove(event: PlayerMoveEvent) {
        if (state.listen) return
        val to = event.to ?: return
        if (event is PlayerTeleportEvent && event.to!!.world != event.from.world) {
            event.isCancelled = true
            PL.sendMsgWithPrefix(p, Lang["game.prevent_teleport"])
            return
        }
        if (state == GameState.PREPARE && to.y <= startY) {
            state = GameState.GAMING
            return
        }
        val block = p.location.add(0.0, -0.2, 0.0).block
        if (block.isEmpty) return
        if (block.type.name.formatAsConst() in cfg.target) {
            cfg.command.onSuccess(p)
            stop()
            return
        }
        cfg.command.onFail(p)
        stop()
    }

    /**
     * 游戏结束
     */
    fun stop() {
        state = GameState.FINISH
        p.teleport(location)
        clear()
        GameManager.games.remove(this)
    }

    fun clear() = locations.forEach(GameLocation::clear)
}