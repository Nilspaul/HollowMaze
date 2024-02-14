package com.hollowmaze.game.controller.bush

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.hollowmaze.game.R
import com.hollowmaze.game.controller.helper.Object
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.Size

class Bush(context: Context, position: Position, size: Size) : Object(position, size) {
    private val originalBitmap: Bitmap
    private val scaleFactor = 0.5 // Faktor, um das Bitmap um den Faktor 10 zu verkleinern
    var scaledBitmap : Bitmap
    init {
        originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bush)
        val width = (originalBitmap.width * scaleFactor).toInt()
        val height = (originalBitmap.height * scaleFactor).toInt()
        scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false)

        size.width = width.toFloat()
        size.height = height.toFloat()
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(scaledBitmap, position.x, position.y, null)
    }
}