package com.hollowmaze.game.model.player

import android.graphics.Bitmap
import com.hollowmaze.game.controller.helper.Size
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.Object
import com.hollowmaze.game.controller.helper.SettingsHelper
import com.hollowmaze.game.model.GameModel

open class PlayerModel(var model: GameModel, position: Position, size:Size = Size(100f, 100f)) : Object(position, size) {

    companion object {
        const val DIRECTION_RIGHT = 1
        const val DIRECTION_LEFT = 2
        const val DIRECTION_BOTTOM = 3
        const val DIRECTION_TOP = 4
        const val DIRECTION_STANDING = 0
    }


    // Bewegungsrichtung
    var moveDirection: Pair<Float, Float>? = null
    var char = arrayOfNulls<Bitmap>(12)
    var rechar = arrayOfNulls<Bitmap>(12)
    var movingDirection: Int = DIRECTION_STANDING

    var previousPositions = mutableListOf<Position>()

    var searchPath = mutableListOf<Pair<Int, Int>>()
    val enemyMovmentSpeedDefault = 0 // Wait pro Position in Millisekunden
    var enemyMovmentSizeDefault = SettingsHelper.currentDifficulty.enemyMovmentSize // Sprünge zwischen den Kacheln
    var enemyMovmentSpeed = enemyMovmentSpeedDefault
    var enemyMovmentSize = enemyMovmentSizeDefault

    var playerCollisionTolerance = size.centerX
    var isHiddenInBush = false
    var hasFiredPing = false
    var hasUnlimitedPings = false

    var charframe: Int = 0
        get() = field
        set(value) {
            field = value
        }



    /**
     * Gibt an, wann welcher Frame des Chars angezeigt wird
     */
    fun calcCharFrame() {
        // Delay zwischen den Zuständen
        val stateDuration = 150
        val totalStates = char.size

        // Zustand in Abhängigkeit der Systemzeit berechnen
        if(movingDirection == DIRECTION_LEFT) {
            charframe = ((System.currentTimeMillis() / stateDuration) % 3).toInt() + 3
        } else if(movingDirection == DIRECTION_RIGHT) {
            charframe = ((System.currentTimeMillis() / stateDuration) % 3).toInt() + 6
        } else if (movingDirection == DIRECTION_TOP) {
            charframe = ((System.currentTimeMillis() / stateDuration) % 3).toInt() + 9
        } else {
            charframe = ((System.currentTimeMillis() / stateDuration) % 3).toInt()
        }
    }

    /**
     * Bewegt den Spieler um X/Y Koordinaten von seiner aktuellen Position aus
     */
    fun move(deltaX: Float, deltaY: Float) {
        updatePosition(position.x + deltaX, position.y + deltaY)
        setMovingDirection(deltaX, deltaY)
    }


    fun setMovingDirection(deltaX: Float, deltaY: Float) {
        // Bestimme die vorherrschende Richtung basierend auf den Beträgen von deltaX und deltaY
        val absDeltaX = Math.abs(deltaX)
        val absDeltaY = Math.abs(deltaY)
        var direction = when {
            absDeltaX > absDeltaY && deltaX > 0 -> DIRECTION_RIGHT
            absDeltaX > absDeltaY && deltaX < 0 -> DIRECTION_LEFT
            absDeltaY > absDeltaX && deltaY > 0 -> DIRECTION_BOTTOM
            absDeltaY > absDeltaX && deltaY < 0 -> DIRECTION_TOP
            else -> DIRECTION_STANDING
        }
        movingDirection = direction
    }
    /**
     *
     * Aktualisiert die Position eines Spielers
     */
    fun updatePosition(destX: Float, destY: Float) {
        setPosition(destX, destY)
    }

    /**
     * Liefert alle Objekte mit denen der Spieler kollidiert
     */
    fun getCollisions(objects: List<Object>): List<Object> {
        return objects.filter { it != this && it.boundingBox.intersect(this.boundingBox) }
    }

    /**
     * Setzt die Position eines Spielers
     */
    private fun setPosition(destX: Float, destY: Float) {
        position.x = destX
        position.y = destY
    }

    fun addPreviousPosition(position: Position) {
        val maxSize = 10
        if (previousPositions.size >= maxSize) {
            previousPositions.removeAt(0)
        }
        previousPositions.add(position)
    }

}