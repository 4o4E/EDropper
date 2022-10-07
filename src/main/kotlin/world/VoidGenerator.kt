package top.e404.edropper.world

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import java.util.*

object VoidGenerator : ChunkGenerator() {
    override fun generateChunkData(
        world: World,
        random: Random,
        x: Int,
        z: Int,
        biome: BiomeGrid
    ) = Bukkit.createChunkData(world)
}