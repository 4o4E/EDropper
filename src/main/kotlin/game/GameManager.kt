package top.e404.edropper.game

import com.sk89q.worldedit.bukkit.BukkitAdapter
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import top.e404.edropper.PL
import top.e404.edropper.config.Config
import java.util.concurrent.ConcurrentHashMap
import com.sk89q.worldedit.world.World as WeWorld

object GameManager {
    lateinit var world: World
    lateinit var weWorld: WeWorld

    val games = mutableSetOf<Game>()
    lateinit var task: BukkitTask

    fun startTick() {
        task = PL.runTaskTimer(1, 1) {
            games.forEach(Game::onTick)
        }
    }

    operator fun get(p: Player) = games.firstOrNull { it.p == p }

    /**
     * 区域及占用该区域的游戏
     */
    val locations = ConcurrentHashMap<GameLocation, Game>()

    /**
     * 查找未占用的区域, 并直接占用
     *
     * @param game 属于的游戏
     * @param block 对区域进行操作
     * @return
     */
    fun getAndUse(game: Game, block: (GameLocation) -> Unit): GameLocation {
        var i = 0
        while (true) {
            if (i == 0) {
                val key = GameLocation(0, 0)
                if (!locations.containsKey(key)) {
                    locations[key] = game
                    game.locations.add(key)
                    block(key)
                    return key
                }
            }
            for (x in -i..i) for (y in -i..i) {
                if (x != -i
                    && x != i
                    && y != -i
                    && y != i
                ) continue
                val key = GameLocation(x, y)
                if (locations.containsKey(key)) continue
                locations[key] = game
                game.locations.add(key)
                block(key)
                return key
            }
            i++
        }
    }

    /**
     * 初始化世界
     */
    fun initWorld() {
        world = Bukkit.createWorld(WorldCreator(Config.world).apply {
            generator(VoidGenerator)
        })!!
        weWorld = BukkitAdapter.adapt(world)
    }

    fun shutdown() {
        games.toList().forEach(Game::stop)
    }
}