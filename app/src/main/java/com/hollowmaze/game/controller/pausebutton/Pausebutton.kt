package com.hollowmaze.game.controller.pausebutton

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import com.hollowmaze.game.R
import com.hollowmaze.game.model.GameModel

class PauseButton(context: Context, var gameModel: GameModel) {
    private val originalBitmap: Bitmap
    private val scaleFactor = 0.15f // Faktor, um das Bitmap um den Faktor 10 zu verkleinern
    private var x = gameModel.viewport.x
    private var y = gameModel.viewport.y
    init {
        originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pausebutton)
    }

    fun draw(canvas: Canvas) {
        x = gameModel.viewport.x + 30f
        y = gameModel.viewport.y + 30f
        //Log.v("InitPause", "${x}, ${y}")
        val width = (originalBitmap.width * scaleFactor).toInt()
        val height = (originalBitmap.height * scaleFactor).toInt()

        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false)

        canvas.drawBitmap(scaledBitmap, x, y, null)
    }
    fun onClick(event: MotionEvent) : Boolean {
        val eventX = event.x + gameModel.viewport.x
        val eventY = event.y + gameModel.viewport.y

        val buttonWidth = (originalBitmap.width * scaleFactor).toInt()
        val buttonHeight = (originalBitmap.height * scaleFactor).toInt()

        if (eventX >= x && eventX <= x + buttonWidth && eventY >= y && eventY <= y + buttonHeight) {
            return true
        } else {
            return false
        }
    }
}
