package com.hollowmaze.game.controller.items

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import com.hollowmaze.game.R
import com.hollowmaze.game.controller.cooldown.Cooldown
import com.hollowmaze.game.controller.helper.Object
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.Size
import com.hollowmaze.game.controller.player.PlayerController
import com.hollowmaze.game.model.GameModel
import com.hollowmaze.game.model.items.EyeModel

class Eye(context: Context, position: Position, size: Size, gameModel: GameModel) : ItemController(
    gameModel,
) {
    init {
        model = EyeModel(gameModel,context, position, size)

        model.scaledBitmap = model.originalBitmap?.let { Bitmap.createScaledBitmap(it, size.width.toInt(), size.height.toInt(), false) }
    }
    override fun draw(canvas: Canvas, gameModel: GameModel) {
        if(isPickedUp) {
            model.scaledBitmap?.let { canvas.drawBitmap(it, gameModel.viewport.x + 300f, gameModel.viewport.y + gameModel.window.height/1.07f, null) }
        } else if(!isUsed){
            model.scaledBitmap?.let { canvas.drawBitmap(it, model.position.x, model.position.y, null) }
        }
    }
}