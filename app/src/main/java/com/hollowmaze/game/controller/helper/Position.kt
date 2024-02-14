package com.hollowmaze.game.controller.helper

import kotlin.math.*

/**
 * Pasition (koordinatenbasiert)
 */
class Position(var x: Float, var y: Float) {

    /**
     * Liefert den Abstand zu einer Position
     */
    fun distanceTo(other: Position): Float {
        val dx = x - other.x
        val dy = y - other.y
        return sqrt(dx * dx + dy * dy)
    }

    /**
     * Liefert den Mittelpunkt zu einer anderen Position
     */
    fun midTo(other: Position): Position {
        return Position((x + other.x) / 2, (y + other.y) / 2)
    }

    /**
     * Verschiebt die Position um einen Delta-Wert X & Y
     */
    fun translate(deltaX: Float, deltaY: Float): Position {
        return Position(x + deltaX, y + deltaY)
    }

    /**
     * Setzt die Position auf Grundlage einer neuen Position
     */
    fun set(newPosition: Position) {
        x = newPosition.x
        y = newPosition.y
    }

    /**
     * Aktualisiert die Position anhand zwei Werten
     */
    fun update(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    /**
     * Normalisierung
     */
    fun normalize(): Position {
        val magnitude = sqrt(x * x + y * y)
        return if (magnitude == 0f) this else Position(x / magnitude, y / magnitude)
    }

    /**
     * Liefert den Winkel zu einer anderen Position
     */
    fun angleTo(other: Position): Float {
        val dx = other.x - x
        val dy = other.y - y
        return Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    }

    /**
     * Begrenzt die Position durch min/max
     */
    fun clamp(minX: Float, maxX: Float, minY: Float, maxY: Float): Position {
        return Position(x.coerceIn(minX, maxX), y.coerceIn(minY, maxY))
    }

    override fun toString(): String {
        return "(${x};${y})"
    }
}