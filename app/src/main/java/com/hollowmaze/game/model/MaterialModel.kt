package com.hollowmaze.game.model

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.hollowmaze.game.controller.helper.Object
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.Size

/**
 * TODO: Aufteilung oder Vererbung in Klassen zu Walls, Doors etc.
 */
class MaterialModel(position: Position, size: Size, val color: Int) : Object(position, size) {

    var orientation: Int = -1
    var bitmapRef: Int = -1

    // Farbe
    var paint = Paint().apply {
        this.color = this@MaterialModel.color
    }

    // TEMP: für Farbwechsel
    var defaultColor: Int = paint.color

    // Größe des Rechtecks
    var rect = boundingBox

    /**
     * Zeichnet das Rechteckige-Objekt
     */
    fun draw(canvas: Canvas) {
        canvas.drawRect(rect, paint)
    }


    fun updateColor(newColor: Int) {
        paint.color = newColor
    }

    fun updateRefBitmap(id: Int) {
        bitmapRef = id
    }

    //Erzeugt Kanten der Box
    fun getEdges() = listOf(
        Edge(Vector2(position.x, position.y), Vector2(position.x + size.width, position.y)),
        Edge(
            Vector2(position.x + size.width, position.y),
            Vector2(position.x + size.width, position.y + size.height)
        ),
        Edge(
            Vector2(position.x + size.width, position.y + size.height),
            Vector2(position.x, position.y + size.height)
        ),
        Edge(Vector2(position.x, position.y + size.height), Vector2(position.x, position.y))

    )

    //Checkt Collision mit Ping
    fun isSquareCollidingWithPing(ping: Ping):
            CollisionResult? {

        val minCollisionresult = getEdges().mapNotNull {
            it.isEdgeCollidingWithEdge(
                Edge(
                    ping.startPos,
                    ping.getEndPosition()
                )
            )
        }
            .minByOrNull {
                it.collisionPoint.getDistance(ping.startPos)

            }

        if(minCollisionresult!=null){
            ping.hasCollided = true
            ping.endPos = minCollisionresult.collisionPoint
        }

        return minCollisionresult
    }

    fun getObjectOrientation(
        currentObj: MaterialModel,
        objects: MutableList<MaterialModel>,
        tileSize: Float = 0f
    ): Int {

        fun hasNeighbor(offsetX: Float, offsetY: Float): Boolean {
            val predictedBox = RectF(
                currentObj.boundingBox.left + offsetX,
                currentObj.boundingBox.top + offsetY,
                currentObj.boundingBox.right + offsetX,
                currentObj.boundingBox.bottom + offsetY
            )
            return objects.any { it.boundingBox.intersect(predictedBox) && it != currentObj }
        }

        val hasTopNeighbor = hasNeighbor(0f, -tileSize)
        val hasBottomNeighbor = hasNeighbor(0f, tileSize)
        val hasLeftNeighbor = hasNeighbor(-tileSize, 0f)
        val hasRightNeighbor = hasNeighbor(tileSize, 0f)
        val hasTopLeftNeighbor = hasNeighbor(-tileSize, -tileSize)
        val hasTopRightNeighbor = hasNeighbor(tileSize, -tileSize)
        val hasBottomLeftNeighbor = hasNeighbor(-tileSize, tileSize)
        val hasBottomRightNeighbor = hasNeighbor(tileSize, tileSize)

        return when {
            !hasTopNeighbor && !hasLeftNeighbor -> 1
            !hasTopNeighbor && hasLeftNeighbor && hasRightNeighbor -> 2
            !hasTopNeighbor && !hasRightNeighbor && hasBottomNeighbor && hasLeftNeighbor -> 3
            !hasLeftNeighbor && hasTopNeighbor && hasBottomNeighbor && hasRightNeighbor -> 4
            hasLeftNeighbor && hasRightNeighbor && hasBottomNeighbor && hasTopNeighbor && !hasTopLeftNeighbor -> 10
            hasLeftNeighbor && hasRightNeighbor && hasBottomNeighbor && hasTopNeighbor && !hasTopRightNeighbor -> 11
            hasLeftNeighbor && hasRightNeighbor && hasBottomNeighbor && hasTopNeighbor && !hasBottomLeftNeighbor -> 12
            hasLeftNeighbor && hasRightNeighbor && hasBottomNeighbor && hasTopNeighbor && !hasBottomRightNeighbor -> 13
            hasTopNeighbor && hasBottomNeighbor && hasLeftNeighbor && hasRightNeighbor -> 5
            !hasRightNeighbor && hasTopNeighbor && hasBottomNeighbor && hasLeftNeighbor -> 6
            !hasLeftNeighbor && !hasBottomNeighbor && hasTopNeighbor && hasRightNeighbor -> 7
            !hasBottomNeighbor && hasTopNeighbor && hasLeftNeighbor && hasRightNeighbor -> 8
            !hasBottomNeighbor && !hasRightNeighbor && hasTopNeighbor && hasLeftNeighbor -> 9
            else -> 5
        }
    }

}
