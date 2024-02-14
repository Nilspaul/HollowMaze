package com.hollowmaze.game.model.search
class PathFinder(private val algorithm: PathFinding) {
    fun getPath(start: Pair<Int, Int>, end: Pair<Int, Int>): List<Pair<Int, Int>> {
        return algorithm.search(start, end)
    }
}