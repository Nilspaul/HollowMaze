package com.hollowmaze.game.controller.player

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.hollowmaze.game.model.GameModel
import com.hollowmaze.game.model.player.PlayerModel

open class PlayerController(gameModel: GameModel) {
    lateinit var model: PlayerModel

    /**
     * Zeichnet, den Player (Bitmap)
     */
    open fun draw(canvas: Canvas) {
        model.calcCharFrame()

        model.rechar[model.charframe]?.let {
            canvas.drawBitmap(it, model.position.x, model.position.y, null)
        }
    }

    /**
     * DEBUG: Zeichnet eine BoundingBox um den Player, um deren echte Größe sichtbar darzustellen
     */
    fun debug_drawBoundingbox(canvas: Canvas) {
        canvas.drawRect(model.position.x, model.position.y, model.position.x + model.size.width, model.position.y + model.size.height, Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
        })
    }
}
