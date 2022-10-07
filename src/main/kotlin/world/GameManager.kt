package top.e404.edropper.world

import com.sk89q.worldedit.bukkit.BukkitAdapter
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import top.e404.edropper.config.Config
import com.sk89q.worldedit.world.World as WrWorld

object GameManager {
    lateinit var world: World
    lateinit var weWorld: WrWorld

    val games = mutableSetOf<Game>()

    fun initWorld() {
        world = Bukkit.createWorld(WorldCreator(Config.world).apply {
            generator(VoidGenerator)
        })!!
        weWorld = BukkitAdapter.adapt(world)
    }

    fun createGame() {

    }

    fun closeGame(game: Game) {
        game.
    }
}