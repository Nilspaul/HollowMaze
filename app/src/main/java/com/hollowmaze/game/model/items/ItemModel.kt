package com.hollowmaze.game.model.items

import android.graphics.Bitmap
import com.hollowmaze.game.controller.helper.Object
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.Size
import com.hollowmaze.game.model.GameModel

open class ItemModel(var model: GameModel, position: Position, size: Size = Size(100f, 100f)) : Object(position, size) {

    var originalBitmap: Bitmap? = null
    val scaleFactor = 0.15// Faktor, um das Bitmap um den Faktor 10 zu verkleinern
    var scaledBitmap : Bitmap? = null
}