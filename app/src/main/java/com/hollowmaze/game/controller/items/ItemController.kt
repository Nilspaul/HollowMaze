package com.hollowmaze.game.controller.items

import android.graphics.Canvas
import com.hollowmaze.game.model.GameModel
import com.hollowmaze.game.model.items.ItemModel

open class ItemController(var gameModel: GameModel) {
    lateinit var model: ItemModel
    val collisionThreshold = 150
    var isPickedUp = false
    var isUsed = false
    open fun draw(canvas: Canvas, gameModel: GameModel) {
        model.originalBitmap?.let { canvas.drawBitmap(it, gameModel.viewport.x + 300f, gameModel.viewport.y + gameModel.window.height/1.25f, null) }
    }
    open fun collisionWithPlayer() {
        val distance = model.center.distanceTo(gameModel.player.model.center)
        if ((distance <= collisionThreshold) && !isUsed) {
            isPickedUp = true
            gameModel.player.model.hasUnlimitedPings = true
            gameModel.cooldown.addCooldown(gameModel, this)
            isUsed = true
        }
    }
}