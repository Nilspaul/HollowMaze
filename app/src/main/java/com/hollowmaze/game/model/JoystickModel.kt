package com.hollowmaze.game.model

import kotlin.math.min
import kotlin.math.sqrt

/**
 * Model für den Joystick, beinhaltet Berechnungen
 */
class JoystickModel {

    var centerX = 0f
    var centerY = 0f

    // First: Minus = Links, Plus = Rechts; Second: Minus = Oben, Plus = Unten
    var direction: Pair<Float, Float> = Pair(0f, 0f)

    val outerRadius = 200f
    val innerRadiusWidth = 10f
    val innerRadius = outerRadius * 0.3f
    val radius = outerRadius * 0.7f

    val ORIENTATION_NEUTRAL = 0
    val ORIENTATION_TOP = 1
    val ORIENTATION_BOTTOM = 2
    val ORIENTATION_LEFT = 3
    val ORIENTATION_RIGHT = 4

    /**
     * Liefert die Richtung der Joystick-Ausrichtung
     */
    fun getDirection(eventX: Float, eventY: Float): Pair<Float, Float> {
        val deltaX = eventX - centerX
        val deltaY = eventY - centerY

        val distance = sqrt(deltaX * deltaX + deltaY * deltaY)

        if (distance == 0f) {
            return Pair(0f, 0f)
        }

        // Sensitive Bewegung des Joysticks durch Normalizierung
        val normalizedDistance = min(distance / outerRadius, 1f)

        direction = Pair(
            (deltaX / distance) * normalizedDistance,
            (deltaY / distance) * normalizedDistance
        )
        return direction
    }

    fun getGeneralDirection(): Int {
        // 0=Neutral
        val threshold = 0.1

        return when {
            direction.first > threshold -> ORIENTATION_RIGHT
            direction.first < -threshold -> ORIENTATION_LEFT
            direction.second > threshold -> ORIENTATION_TOP
            direction.second < -threshold -> ORIENTATION_BOTTOM
            else -> ORIENTATION_NEUTRAL
        }
    }

}