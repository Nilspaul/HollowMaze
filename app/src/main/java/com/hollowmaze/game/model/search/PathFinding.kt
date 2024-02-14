package com.hollowmaze.game.model.search

interface PathFinding {
    val maze: Maze
    val stepSize : Int  // Abstände der zu prüfenden Schritte - je größer, desto performanter
    fun search(start: Pair<Int, Int>, end: Pair<Int, Int>): List<Pair<Int, Int>>
}