package com.hollowmaze.game.model

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log

class DirectionBorder(val gameModel: GameModel) {

    val maxDistance = 5_000f
    val ovalRadius = 400f

    var enemyPlayerDist = 0f

    private fun getHorizontalBorders() = listOf(
        Edge(
            Vector2(gameModel.viewport.x, gameModel.viewport.y),
            Vector2(gameModel.viewport.x + gameModel.window.width, gameModel.viewport.y)
        ),
        Edge(
            Vector2(gameModel.viewport.x, gameModel.viewport.y + gameModel.window.height),
            Vector2(gameModel.viewport.x + gameModel.window.width, gameModel.viewport.y + gameModel.window.height)
        ),
    )

    private fun getVerticalBorders() = listOf(
        Edge(
            Vector2(gameModel.viewport.x, gameModel.viewport.y),
            Vector2(gameModel.viewport.x, gameModel.viewport.y + gameModel.window.height)
        ),
        Edge(
            Vector2(gameModel.viewport.x + gameModel.window.width, gameModel.viewport.y),
            Vector2(gameModel.viewport.x + gameModel.window.width, gameModel.viewport.y + gameModel.window.height)
        )
    )


    fun getCollision(): CollisionResult? {

        val enemy = gameModel.enemy?.model
        val player = gameModel.player?.model

        if (enemy != null && player != null) {
            enemyPlayerDist = player.position.toVector().getDistance(enemy.position.toVector())
            if (enemyPlayerDist < maxDistance) {

                val playerToEnemyEdge = Edge(player.position.toVector(), enemy.position.toVector())
                getHorizontalBorders().forEach { edge ->
                    playerToEnemyEdge.isEdgeCollidingWithEdge(edge)?.let {
                        return it
                    }

                    getVerticalBorders().mapNotNull { edge ->
                        playerToEnemyEdge.isEdgeCollidingWithEdge(edge)?.let {
                            return it
                        }
                    }
                }
            }
        }
        return null
    }
}