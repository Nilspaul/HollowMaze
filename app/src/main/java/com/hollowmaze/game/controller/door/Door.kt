package com.hollowmaze.game.controller.door

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.hollowmaze.game.R
import com.hollowmaze.game.controller.activities.LevelselectionActivity
import com.hollowmaze.game.controller.helper.LevelHelper
import com.hollowmaze.game.controller.helper.Object
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.Size
import com.hollowmaze.game.model.GameModel
import com.hollowmaze.game.model.player.PlayerModel

class Door(private val context: Context, var gameModel: GameModel, position: Position = Position(0f, 0f), size: Size = Size(100f, 100f)) : Object(position, size) {
    private val originalBitmap: Bitmap
    private val scaleFactor = 0.8f // Faktor, um das Bitmap um den Faktor 10 zu verkleinern
    var collisionThreshold = 100f // Bereich indem es als Collision erkannt wird
    lateinit var scaledBitmap : Bitmap
    var unlocked = false
    init {
        originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.door_front)
        val width = (originalBitmap.width * scaleFactor).toInt()
        val height = (originalBitmap.height * scaleFactor).toInt()
        scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false)

        size.width = width.toFloat()
        size.height = height.toFloat()

        collisionThreshold = size.centerX
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(scaledBitmap, position.x, position.y, null)
    }

    fun canOpenDoor() : Boolean {
        gameModel.key?.isPickedUp.let {
            if(it == true) {
                 return true
            }
        }
        return false
    }

    fun collisionWithPlayer(player: PlayerModel) {
        if(unlocked == false) {
            val distance = center.distanceTo(player.center)
            if ((distance < collisionThreshold) && canOpenDoor()) {
                unlocked = true
                LevelHelper.setAccessibilityOfNextLevel(context)
                val intent = Intent(context, LevelselectionActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            }
        }
    }
}

