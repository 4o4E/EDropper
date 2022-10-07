package top.e404.edropper.world

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import top.e404.edropper.config.Config
import top.e404.edropper.config.GameConfig
import top.e404.eplugin.hook.worldedit.pasteCenter
import com.sk89q.worldedit.world.World as WeWorld


class GameWorld(val worldName: String, config: GameConfig) {
    lateinit var world: World
    lateinit var weWorld: WeWorld

    val config = config.absolute

    fun create() {
        world = Bukkit.createWorld(WorldCreator(worldName).apply {
            generator(VoidGenerator)
        })!!
        weWorld = BukkitAdapter.adapt(world)
    }

    fun generator() {
        var y = Config.lowest
        WorldEdit.getInstance().newEditSession(weWorld).use { session ->

            config.bottom.apply {
                pasteCenter(session, 0, y, 0)
                y += dimensions.y
            }
            config.group.keys.forEach { g ->
                g.pasteCenter(session, 0, y, 0)
                y += g.dimensions.y
            }

            config.top.apply {
                pasteCenter(session, 0, y, 0)
                y += dimensions.y
            }
        }
    }
}