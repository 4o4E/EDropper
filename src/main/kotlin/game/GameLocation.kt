package top.e404.edropper.game

import top.e404.edropper.config.Config
import top.e404.edropper.hook.WeHook

/**
 * 代表一块游戏区域
 *
 * @property x 游戏中心的x坐标, 实际坐标为 x * radius
 * @property z 游戏中心的z坐标, 实际坐标为 z * radius
 */
data class GameLocation(
    val x: Int,
    val z: Int
) {
    /**
     * 区域最小x坐标
     */
    val fromX by lazy { (x * 2 - 1) * Config.radius }

    /**
     * 区域最小y坐标
     */
    val fromZ by lazy { (z * 2 - 1) * Config.radius }

    /**
     * 区域最大x坐标
     */
    val toX by lazy { (x * 2 + 1) * Config.radius }

    /**
     * 区域最大y坐标
     */
    val toZ by lazy { (z * 2 + 1) * Config.radius }

    /**
     * 区域中心x坐标
     */
    val centerX by lazy { x * 2 * Config.radius }

    /**
     * 区域中心y坐标
     */
    val centerZ by lazy { z * 2 * Config.radius }

    /**
     * 清空对应区域
     */
    fun clear() = WeHook.clear(GameManager.world, fromX, Config.lowest, fromZ, toX, Config.highest, toZ)
}