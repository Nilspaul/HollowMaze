package com.hollowmaze.game.view
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.hollowmaze.game.R
import com.hollowmaze.game.controller.helper.Box
import com.hollowmaze.game.model.GameModel
import com.hollowmaze.game.model.ViewRendererModel

class ViewRenderer(context: Context) {

    var canvas: Canvas? = null
    var model = ViewRendererModel()

    var visibleArea = RectF(0f,0f,0f,0f)

    val fg_boxes = mutableListOf<Box>()
    val bg_boxes = mutableListOf<Box>()

    fun add(obj: Box) {
        bg_boxes.add(obj)
    }

    fun add_foreground(obj: Box) {
        fg_boxes.add(obj)
    }

    fun add_background(obj: Box) {
        bg_boxes.add(obj)
    }

    fun update(gameModel: GameModel) {
        visibleArea = RectF(gameModel.viewport.x - model.toleranz, gameModel.viewport.y - model.toleranz, gameModel.viewport.x + gameModel.window.width + model.toleranz, gameModel.viewport.y + gameModel.window.height + model.toleranz)
    }

    fun draw(canvas: Canvas) {
        drawBackground(canvas)
        drawForeground(canvas)
    }

    fun drawForeground(canvas: Canvas) {
        var elementsInView = 0
        // Log.v("render", "${visibleArea.toString()} - ${viewport.toString()} - ${window.toString()}")
        for (box in fg_boxes) {
            if (RectF.intersects(visibleArea, box.boundingBox)) {
                // Log.v("render", "Draw: ${box.rect.toString()}")
                drawBox(canvas, box)

                elementsInView++
            }
        }
        //Log.v("render", "FG-Elements im Viewport: ${elementsInView} von ${fg_boxes.size}")
    }

    fun drawBackground(canvas: Canvas) {
        var elementsInView = 0
        // Log.v("render", "${visibleArea.toString()} - ${viewport.toString()} - ${window.toString()}")
        for (box in bg_boxes) {
            if (RectF.intersects(visibleArea, box.boundingBox)) {
                drawBox(canvas, box)
                elementsInView++
            }
        }
        //Log.v("render", "BG-Elements im Viewport: ${elementsInView} von ${bg_boxes.size}")
    }

    fun drawBox(canvas: Canvas, box: Box) {
        val bitmap = box.bitmap
        if(bitmap is Bitmap) {
            canvas?.drawBitmap(bitmap, box.position.x, box.position.y, null)
        } else {
            canvas?.drawRect(box.boundingBox, box.paint)
        }
    }
}