package com.hollowmaze.game.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.hollowmaze.game.R

class WallView(context: Context, width: Float = 100f, height: Float = 100f) : ObjectView(context) {

    init {
        drawableRefs["1"] = R.drawable.maze_stone_tile1 // Oben-Links
        drawableRefs["2"] = R.drawable.maze_stone_tile2 // Oben-Mitte
        drawableRefs["3"] = R.drawable.maze_stone_tile3 // Oben-Rechts
        drawableRefs["4"] = R.drawable.maze_stone_tile4 // Links
        drawableRefs["5"] = R.drawable.maze_stone_tile5 // Mitte
        drawableRefs["6"] = R.drawable.maze_stone_tile6 // Rechts
        drawableRefs["7"] = R.drawable.maze_stone_tile7 // Unten-Links
        drawableRefs["8"] = R.drawable.maze_stone_tile8 // Unten
        drawableRefs["9"] = R.drawable.maze_stone_tile9 // Unten-Rechts
        drawableRefs["10"] = R.drawable.maze_stone_tile10 // Oben-Links-Inner
        drawableRefs["11"] = R.drawable.maze_stone_tile11 // Oben-Rechts-Inner
        drawableRefs["12"] = R.drawable.maze_stone_tile12 // Unten-Links-Inner
        drawableRefs["13"] = R.drawable.maze_stone_tile13 // Unten-Rechts-Inner

        // Bitmaps speichern
        drawableRefs.forEach { index, ref ->
            bitmaps[ref.toString()] = BitmapFactory.decodeResource(context.resources, ref)
        }

        // Bitmaps auf Zielgröße skalieren
        bitmaps.forEach { index, bitmap ->
            scaledBitmap[index] = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), true)
        }

    }

}