package com.hollowmaze.game.model.search

class DFSAlgorithm(override val maze: Maze) : PathFinding {
    override val stepSize : Int = 100

    override fun search(start: Pair<Int, Int>, end: Pair<Int, Int>): List<Pair<Int, Int>> {
        val stack = mutableListOf<Pair<Int, Int>>()
        val visited = mutableSetOf<Pair<Int, Int>>()
        val path = mutableListOf<Pair<Int, Int>>()

        // Clearing - Sicherheitshalber!
        stack.clear()
        visited.clear()
        path.clear()

        stack.add(start)

        while (stack.isNotEmpty()) {
            val current = stack.removeLast()
            if (current in visited) continue
            visited.add(current)

            if (current == end) {
                path.add(current)
                break
            }

            val (x, y) = current
            listOf(
                Pair(x + stepSize, y),
                Pair(x - stepSize, y),
                Pair(x, y + stepSize),
                Pair(x, y - stepSize)
            ).filter {
                it.first in 0 until maze.width &&
                        it.second in 0 until maze.height &&
                        it !in visited &&
                        !maze.isWall(it.first, it.second)
            }.forEach { stack.add(it) }

            path.add(current)
        }
        return path
    }
}