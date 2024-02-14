package com.hollowmaze.game.model.search

import android.util.Log
import kotlin.math.*

class AStarAlgorithm(override val maze: Maze) : PathFinding {
    override val stepSize : Int = 100

    private val openSet = mutableSetOf<Pair<Int, Int>>()
    private val cameFrom = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>?>()
    private val gScore = mutableMapOf<Pair<Int, Int>, Double>().withDefault { Double.POSITIVE_INFINITY }
    private val fScore = mutableMapOf<Pair<Int, Int>, Double>().withDefault { Double.POSITIVE_INFINITY }

    override fun search(start: Pair<Int, Int>, end: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
        Log.d("AStar", "Starte Suche von $start bis $end")

        // Clearing - sicherheitshalber
        openSet.clear()
        cameFrom.clear()
        gScore.clear()
        fScore.clear()

        openSet.add(start)
        gScore[start] = 0.0
        fScore[start] = heuristic(start, end)

        while (openSet.isNotEmpty()) {
            val current = openSet.minByOrNull { fScore.getValue(it) } ?: break
            //Log.d("AStar", "Knoten betrachten: $current")

            if (current == end) {
                Log.d("AStar", "Ziel gefunden, Pfad rekonstruieren")
                return reconstructPath(cameFrom, current).toMutableList()
            }

            openSet.remove(current)
            getNeighbors(current, maze.walls).forEach { neighbor ->
                val tentativeGScore = gScore.getValue(current) + distBetween(current, neighbor)
                if (tentativeGScore < gScore.getValue(neighbor)) {
                    cameFrom[neighbor] = current
                    gScore[neighbor] = tentativeGScore
                    fScore[neighbor] = tentativeGScore + heuristic(neighbor, end)
                    if (neighbor !in openSet) openSet.add(neighbor)
                }
            }
        }

        Log.d("AStar", "Kein Pfad gefunden")
        return mutableListOf()
    }

    private fun getNeighbors(node: Pair<Int, Int>, walls: Set<Pair<Int, Int>>): List<Pair<Int, Int>> {
        val (x, y) = node
        return listOf(
            Pair(x - stepSize, y), Pair(x + stepSize, y), Pair(x, y - stepSize), Pair(x, y + stepSize)
        ).filter {
            it.first in 0 until maze.width && it.second in 0 until maze.height && it !in walls
        }
    }

    /**
     * Distanzberechnung für Scoreing
     */
    private fun distBetween(start: Pair<Int, Int>, end: Pair<Int, Int>): Double {
        return sqrt((end.first - start.first).toDouble().pow(2) + (end.second - start.second).toDouble().pow(2))
    }

    /**
     * Manhattan-Distanz als Heuristik
     */
    private fun heuristic(node: Pair<Int, Int>, goal: Pair<Int, Int>): Double {
        return abs(node.first - goal.first) + abs(node.second - goal.second).toDouble()
    }

    /**
     * Pfad Rekonstruierung
     */
    private fun reconstructPath(cameFrom: MutableMap<Pair<Int, Int>, Pair<Int, Int>?>, current: Pair<Int, Int>): List<Pair<Int, Int>> {
        val path = mutableListOf(current)
        var currentVar = current
        val visited = mutableSetOf<Pair<Int, Int>>()

        while (cameFrom[currentVar] != null) {
            currentVar = cameFrom[currentVar]!!

            // Überprüfen, ob der Knoten bereits besucht wurde
            if (currentVar in visited) {
                Log.v("AStar", "Mögliche Endlosschleife erkannt - derselbe Knoten wurde mehr als einmal besucht. - ${path}")
                return path
            }

            visited.add(currentVar)
            path.add(0, currentVar)
        }

        return path
    }
}