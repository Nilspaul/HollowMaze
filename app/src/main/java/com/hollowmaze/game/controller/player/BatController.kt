package com.hollowmaze.game.controller.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.hollowmaze.game.R
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.Size
import com.hollowmaze.game.model.player.BatModel
import com.hollowmaze.game.model.GameModel

class BatController (context: Context, gameModel: GameModel) : PlayerController(gameModel) {
    val org_bitmap_width = 199
    val org_bitmap_height = 164

    init {
        model = BatModel(gameModel, Position(0f,0f), Size(199f, 164f))
        model.size = model.size.scale(1.5f)
        // Lädt die Bitmaps rein
        model.char[0] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_front_1)
        model.char[1] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_front_2)
        model.char[2] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_front_3)
        model.char[3] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_left_1)
        model.char[4] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_left_2)
        model.char[5] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_left_3)
        model.char[6] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_right_1)
        model.char[7] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_right_2)
        model.char[8] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_right_3)
        model.char[9] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_back_1)
        model.char[10] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_back_2)
        model.char[11] = BitmapFactory.decodeResource(context.resources, R.drawable.bat_back_3)

        // Kalkuliert die Bitmap runter auf Basis des gleichen Seitenverhältnis
        val bitmap_width = model.size.width
        val bitmap_height = model.size.height / org_bitmap_width * org_bitmap_height

        // Resize Bitmap
        model.rechar[0] = model.char[0]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
        model.rechar[1] = model.char[1]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
        model.rechar[2] = model.char[2]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
        model.rechar[3] = model.char[3]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
        model.rechar[4] = model.char[4]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
        model.rechar[5] = model.char[5]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
        model.rechar[6] = model.char[6]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
        model.rechar[7] = model.char[7]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
        model.rechar[8] = model.char[8]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
        model.rechar[9] = model.char[9]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
        model.rechar[10] = model.char[10]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
        model.rechar[11] = model.char[11]?.let { Bitmap.createScaledBitmap(it, bitmap_width.toInt(), bitmap_height.toInt(), true) }
    }
}