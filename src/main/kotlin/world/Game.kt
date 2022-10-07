package top.e404.edropper.world

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.extent.Extent
import com.sk89q.worldedit.extent.clipboard.Clipboard
import top.e404.edropper.config.Config
import top.e404.edropper.config.GameConfig
import top.e404.eplugin.hook.worldedit.pasteCenter
import kotlin.math.max

/**
 * 代表一场游戏
 *
 * @property x 游戏场地中心的x坐标
 * @property z 游戏场地中心的z坐标
 * @param config 对应的游戏设置
 */
class Game(
    val x: Int,
    val z: Int,
    config: GameConfig
) {
    val config = config.absolute

    var w = 0
    var l = 0

    fun generatorSequence() = ArrayList<Clipboard>().let {
        it.add(config.bottom)

        val all = config

        it.add(config.top)
    }

    fun generator() {
        var y = Config.lowest
        WorldEdit.getInstance().newEditSession(GameManager.weWorld).use { session ->

            config.bottom.apply {
                w = max(region.width, w)
                l = max(region.length, l)
                pasteCenter(session, x, y, z)
                y += dimensions.y
            }
            config.group.keys.forEach { g ->
                w = max(g.region.width, w)
                l = max(g.region.length, l)
                g.pasteCenter(session, x, y, z)
                y += g.dimensions.y
            }

            config.top.apply {
                w = max(region.width, w)
                l = max(region.length, l)
                pasteCenter(session, x, y, z)
                y += dimensions.y
            }
        }
    }

    fun reset() {
        GameManager.weWorld
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (x != other.x) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + z
        return result
    }
}