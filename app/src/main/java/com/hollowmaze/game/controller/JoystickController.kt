package com.hollowmaze.game.controller

import android.view.MotionEvent
import kotlin.math.min
import kotlin.math.sqrt

class JoystickController {
    private var active = false
    private var centerX = 0f
    private var centerY = 0f
    private val radius = 20f

    fun startTouch(event: MotionEvent) {
        active = true
        centerX = event.x
        centerY = event.y
    }

    /**
     * Liefert die Richtung (X,Y) des Joystick-Ausrichtung
     */
    fun getDirection(event: MotionEvent): Pair<Float, Float> {
        val deltaX = event.x - centerX
        val deltaY = event.y - centerY

        val distance = sqrt(deltaX * deltaX + deltaY * deltaY)

        if (distance == 0f) {
            return Pair(0f, 0f)
        }

        val normalizedDeltaX = deltaX / distance
        val normalizedDeltaY = deltaY / distance

        return Pair(normalizedDeltaX, normalizedDeltaY)
    }

    fun stopTouch() {
        active = false
    }
}