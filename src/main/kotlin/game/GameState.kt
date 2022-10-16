package top.e404.edropper.game

enum class GameState(val listen: Boolean = false) {
    INITIALIZATION,
    PREPARE(true),
    GAMING(true),
    FINISH
}