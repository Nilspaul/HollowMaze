package com.hollowmaze.game.controller.controls

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import com.hollowmaze.game.model.GameModel
import com.hollowmaze.game.model.JoystickModel
import kotlin.math.sqrt

class JoystickController(context: Context, gameModel: GameModel) {
    val model: JoystickModel = JoystickModel()
    val gameModel: GameModel = gameModel
    var disable: Boolean = false

    val paintInnerCircle= Paint().apply {
        color = Color.argb(90, 211, 211, 211)
        isAntiAlias = true
    }
    val paintOuterCircle = Paint().apply {
        color = Color.argb(80, 211, 211, 211)
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = model.innerRadiusWidth
    }

    private var active = false

    /**
     * Aktiviert den Joystick
     */
    fun startTouch(event: MotionEvent) {
        active = true
        if(!disable) {
            model.centerX = event.x
            model.centerY = event.y
        }
    }

    /**
     * Stoppt den Joystick
     */
    fun stopTouch() {
        active = false
    }

    fun activateTouch() {
        disable = false
        Log.v("joystock", "Activated")
    }

    fun disableTouch() {
        disable = true
        Log.v("joystock", "Disabled")
    }

    /**
     * Zeichnet den Joystick auf dem Canvas
     */
    fun draw(canvas: Canvas) {
        if (active) {
            // Äußeren Ring
            canvas.drawCircle(gameModel.viewport.x + model.centerX, gameModel.viewport.y + model.centerY, model.outerRadius, paintOuterCircle)

            // Inneren Kreis
            val innerCircleX = gameModel.viewport.x + model.centerX + model.direction.first * model.radius
            val innerCircleY = gameModel.viewport.y + model.centerY + model.direction.second * model.radius
            canvas.drawCircle(innerCircleX, innerCircleY, model.innerRadius, paintInnerCircle)

            //Log.v("draw_joystick", "Joystick: ${model.direction}; ${innerCircleX}; ${innerCircleY}")
        }
    }
}
