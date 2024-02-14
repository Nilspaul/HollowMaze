package com.hollowmaze.game.model.items

import android.content.Context
import android.graphics.BitmapFactory
import com.hollowmaze.game.R
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.Size
import com.hollowmaze.game.model.GameModel

class EyeModel (model: GameModel, context: Context, position: Position, size: Size) : ItemModel(model, position, size) {
    init {
        originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.eye)
        size.width = originalBitmap?.width?.times(scaleFactor)?.toFloat()!!
        size.height = originalBitmap?.height?.times(scaleFactor)?.toFloat()!!
    }
}

