package com.hollowmaze.game.model.player

import android.graphics.Bitmap
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.Size
import com.hollowmaze.game.model.GameModel

class OwlModel(model: GameModel, position: Position, size: Size) : PlayerModel(model, position, size) {
    init {
        char = arrayOfNulls<Bitmap>(12)
        rechar = arrayOfNulls<Bitmap>(12)
        super.updatePosition((model.window.width / 2) - size.width, (model.window.height / 2) - size.height)
    }
}