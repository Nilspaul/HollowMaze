package com.hollowmaze.game.controller.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

class BitmapHelper {
    companion object {
        fun getDrawableIdByName(context: Context, resourceName: String): Int {
            return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        }

        fun getDrawableByName(context: Context, resourceName: String) : Drawable? {
            val resID = getDrawableIdByName(context, resourceName)
            if(resID != 0) {
                return ContextCompat.getDrawable(context, resID)
            }
            return null
        }

        fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
            return Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), true)
        }
    }
}