package com.hollowmaze.game.controller.key

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.hollowmaze.game.R
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.Object
import com.hollowmaze.game.controller.helper.Size
import com.hollowmaze.game.model.GameModel
import com.hollowmaze.game.model.player.PlayerModel

class Key(context: Context, var gameModel: GameModel, position: Position = Position(0f, 0f), size:Size = Size(100f, 100f)) : Object(position, size) {
    private val originalBitmap: Bitmap
    private val scaleFactor = 0.3 // Faktor, um das Bitmap um den Faktor 10 zu verkleinern
    var isPickedUp = false

    val collisionThreshold = 100f // Bereich indem es als Collision erkannt wird
    lateinit var scaledBitmap : Bitmap
    init {
        originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.key)
        val width = (originalBitmap.width * scaleFactor).toInt()
        val height = (originalBitmap.height * scaleFactor).toInt()
        scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false)

        size.width = width.toFloat()
        size.height = height.toFloat()
    }

    fun draw(canvas: Canvas) {
        // Schlüssel an Player haften
        if(isPickedUp == true) {
            canvas.drawBitmap(scaledBitmap, gameModel.viewport.x + 30f, gameModel.viewport.y + gameModel.window.height/1.1f, null)
        } else {
            canvas.drawBitmap(scaledBitmap, position.x, position.y, null)
        }
    }

    fun collisionWithPlayer(player: PlayerModel) {
        val distance = center.distanceTo(player.center)
        if ((distance < collisionThreshold) && !isPickedUp) {
            isPickedUp = true
        }
    }
}

