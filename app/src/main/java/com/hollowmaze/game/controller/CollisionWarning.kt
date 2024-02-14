package com.hollowmaze.game.controller

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.hollowmaze.game.model.GameModel

class CollisionWarning(val model: GameModel) {

    val fadeTime = 150f
    var alphaValue = 255f
    var fadeInTimerIsSet = false
    var fadeOutTimerIsSet = false
    var fadeOutTimer = 0L
    var fadeInTimer = 0L

    var lastWarningTimer = 0L
    var warningInterval = 2000L

    @RequiresApi(Build.VERSION_CODES.O)
    fun draw(canvas: Canvas) {
        if (fadeInTimerIsSet||fadeOutTimerIsSet) {
            Log.d("alpha", alphaValue.toString())

            canvas.drawRect(
                RectF(
                    model.viewport.x,
                    model.viewport.y,
                    model.viewport.x + model.window.width,
                    model.viewport.y + model.window.height
                ),
                Paint().apply {
                    color = Color.argb(alphaValue, 1f, 0f, 0f)
                }
            )
        }


    }
}