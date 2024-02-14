package com.hollowmaze.game.model.search

class Maze(val width: Int, val height: Int, val cellSize: Float = 50f) {
    val walls = mutableSetOf<Pair<Int, Int>>()

    fun addWall(x: Int, y: Int) {
        if (x in 0 until width && y in 0 until height) {
            walls.add(Pair(x, y))
        }
    }

    fun isWall(x: Int, y: Int): Boolean = Pair(x, y) in walls
}