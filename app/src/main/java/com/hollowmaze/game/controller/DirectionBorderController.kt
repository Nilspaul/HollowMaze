package com.hollowmaze.game.controller

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import com.hollowmaze.game.model.CollisionResult
import com.hollowmaze.game.model.DirectionBorder
import com.hollowmaze.game.model.GameModel

class DirectionBorderController(val gameModel: GameModel) {
    var model: DirectionBorder = DirectionBorder(gameModel)

    var collision : CollisionResult? = null

    val paint = Paint().apply {
        color = Color.argb(100, 255, 0, 0)
    }

    fun draw(canvas: Canvas) {
        collision?.let {
            val colorOpacity = (255 - ((model.enemyPlayerDist / model.maxDistance) * (255 - 50))).toInt()
            paint.shader = RadialGradient(
                it.collisionPoint.x,
                it.collisionPoint.y,
                model.ovalRadius,
                intArrayOf(Color.argb(colorOpacity, 255, 0, 0), Color.argb(colorOpacity, 255, 0, 0), Color.argb(0, 255, 0, 0)),
                floatArrayOf(0f, 0.7f, 1f),
                Shader.TileMode.CLAMP
            )

            canvas.drawOval(getRectangle(it), paint)
        }
    }

    fun getRectangle(it: CollisionResult): RectF {

        // Vertical
        if((it.collisionPoint.x == gameModel.viewport.x) || (it.collisionPoint.x == gameModel.viewport.x + gameModel.window.width)) {
            return RectF(
                it.collisionPoint.x - (model.ovalRadius / 4 ),
                it.collisionPoint.y - (model.ovalRadius ),
                it.collisionPoint.x + (model.ovalRadius / 4 ),
                it.collisionPoint.y + (model.ovalRadius ),
            )
        }

        // Horizontal
        return RectF(
            it.collisionPoint.x - (model.ovalRadius / 1 ),
            it.collisionPoint.y - (model.ovalRadius / 4 ),
            it.collisionPoint.x + (model.ovalRadius / 1 ),
            it.collisionPoint.y + (model.ovalRadius / 4 ),
        )

        // Original
        return RectF(
            it.collisionPoint.x - (model.ovalRadius / 2 ),
            it.collisionPoint.y - (model.ovalRadius / 2 ),
            it.collisionPoint.x + (model.ovalRadius / 2 ),
            it.collisionPoint.y + (model.ovalRadius / 2 ),
        )
    }
}

