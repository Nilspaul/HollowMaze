package com.hollowmaze.game.controller.helper

import android.graphics.RectF
import kotlin.math.*

/**
 * Ein allgemeines Objekt
 */
open class Object(var position: Position, var size: Size) {

    /**
     * Rückgabe von verschiedenen Punkten/Positionen eines Objects
     */
    val topLeft: Position
        get() = position
    val topRight: Position
        get() = Position(position.x + size.width, position.y)
    val bottomLeft: Position
        get() = Position(position.x, position.y + size.height)
    val bottomRight: Position
        get() = Position(position.x + size.width, position.y + size.height)

    /**
     * Liefert den Mittelpunkt des Objekts
     */
    val center: Position
        get() = Position(position.x + size.width / 2, position.y + size.height / 2)

    /**
     * Liefert die Boundingbox des Objekts
     */
    val boundingBox: RectF
        get() = RectF(position.x, position.y, position.x + size.width, position.y + size.height)


    /**
     * Liefert den Flächengröße des Objekts
     */
    val area: Float
        get() = size.area

    /**
     * Liefert den Umfang des Objekts
     */
    val perimeter: Float
        get() = size.perimeter

    /**
     * Skaliert das Objekt zu einer neuen Größe
     */
    fun resize(newSize: Size) {
        size.resize(newSize)
    }

    /**
     * Skaliert das Objekt um einen Faktor
     */
    fun scale(factor: Float) {
        size.width *= factor
        size.height *= factor
    }

    /**
     * Bewegt das Objekt zu einer neuen Position
     */
    fun moveTo(newPosition: Position) {
        position.x = newPosition.x
        position.y = newPosition.y
    }

    /**
     * Bewegt das Objekt um einen gewissen Offset
     */
    fun moveBy(offsetX: Float, offsetY: Float) {
        position.x += offsetX
        position.y += offsetY
    }

    /**
     * Bewegt das Objekt in eine gewisse Richtung, um eine gewisse Distanz
     */
    fun moveInDirection(direction: Position, distance: Float) {
        val normalizedDirection = direction.normalize()
        position.x += normalizedDirection.x * distance
        position.y += normalizedDirection.y * distance
    }

    /**
     * Prüft, ob ein Objekt mit einem anderen sich überschneidet
     */
    fun intersects(other: Object): Boolean {
        return this.boundingBox.intersects(other.boundingBox.left, other.boundingBox.top, other.boundingBox.right, other.boundingBox.bottom)
    }

    /**
     * Prüft, ob eine Position innerhalb des Objects liegt
     */
    fun containsPoint(point: Position): Boolean {
        return point.x >= position.x && point.x <= position.x + size.width && point.y >= position.y && point.y <= position.y + size.height
    }


    fun getRelativePosition(other: Object): Int {
        val topDistance = abs(boundingBox.bottom - other.boundingBox.top)
        val bottomDistance = abs(boundingBox.top - other.boundingBox.bottom)
        val leftDistance = abs(boundingBox.right - other.boundingBox.left)
        val rightDistance = abs(boundingBox.left - other.boundingBox.right)

        return when {
            topDistance <= listOf(bottomDistance, leftDistance, rightDistance).minOrNull()!! -> 2 // oben
            bottomDistance <= listOf(topDistance, leftDistance, rightDistance).minOrNull()!! -> 3 // unten
            leftDistance <= listOf(topDistance, bottomDistance, rightDistance).minOrNull()!! -> 1// links
            else -> 0 // rechts
        }
    }

    fun getClosestDirection(other: Object): Int {
        val distances = mutableMapOf(
            2 to abs(boundingBox.top - other.boundingBox.bottom), // oben
            3 to abs(boundingBox.bottom - other.boundingBox.top), // unten
            1 to abs(boundingBox.left - other.boundingBox.right), // links
            0 to abs(boundingBox.right - other.boundingBox.left) // rechts
        )

        return distances.minByOrNull { it.value }?.key ?: -1
    }

    /**
     * Liefert den Abstand zu einem anderen Objekt, von Mittelpunkt zu Mittelpunkt
     */
    fun centerDistanceTo(other: Object): Float = this.center.distanceTo(other.center)

    /**
     * Liefert den Abstand zu einem anderen Objekt, vom oberen linken Punkt
     */
    fun topLeftDistanceto(obj: Object): Float = topLeft.distanceTo(obj.topLeft)

    /**
     * Liefert den Abstand zu einem anderen Objekt, vom oberen rechten Punkt
     */
    fun topRightDistanceTo(obj: Object): Float = topRight.distanceTo(obj.topRight)

    /**
     * Liefert den Abstand zu einem anderen Objekt, vom unteren linken Punkt
     */
    fun bottomLeftDistanceTo(obj: Object): Float = bottomLeft.distanceTo(obj.bottomLeft)

    /**
     * Liefert den Abstand zu einem anderen Objekt, vom oberen rechten Punkt
     */
    fun bottomRightDistanceTo(obj: Object): Float = bottomRight.distanceTo(obj.bottomRight)

}