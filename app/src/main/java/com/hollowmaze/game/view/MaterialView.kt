package com.hollowmaze.game.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable

open class ObjectView(context: Context) {

    var drawableRefs: MutableMap<String, Int> = mutableMapOf()
    var bitmaps: MutableMap<String, Bitmap> = mutableMapOf()
    var scaledBitmap: MutableMap<String, Bitmap> = mutableMapOf()

}