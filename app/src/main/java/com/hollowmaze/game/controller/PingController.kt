package com.hollowmaze.game.controller

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.hollowmaze.game.model.Ping

class PingController {
    val pings = mutableListOf<Ping>()
    val maxSize = 3

    val pingColor =  Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = Color.argb(100,255,255,255)
        pathEffect = DashPathEffect(floatArrayOf(10f, 50f), 0f)
    }

    open fun draw(canvas: Canvas) {

        pings.forEachIndexed { index, ping ->
            val pingCurrentEndPos =
                if (index == pings.lastIndex) ping.getEndPosition()
                 else ping.endPos
            canvas.drawLine(
                ping.getStartPosition().x,
                ping.getStartPosition().y,
                pingCurrentEndPos.x,
                pingCurrentEndPos.y,
                pingColor
            )
        }
    }
}