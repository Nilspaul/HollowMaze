package com.hollowmaze.game.controller.helper
import android.graphics.Bitmap
import android.graphics.Color
import kotlin.random.Random

class BitmapExtract(private val bitmap: Bitmap, private val size: Size) {
    companion object {
        val tileSize = 100
    }

    lateinit var scaledBitmap: Bitmap

    val tileSize = BitmapExtract.tileSize
    var tileWidth: Int = 0
    var tileHeight: Int = 0
    val tileMap = mutableMapOf<Int, MutableList<Object>>()

    init {
        // erstmal nur quadratische Tiles
        tileWidth = tileSize
        tileHeight = tileSize
        scaledBitmap = Bitmap.createScaledBitmap(bitmap, size.width.toInt(), size.height.toInt(), true)

        scaledBitmap.let {
            extractTiles(it)
        }

    }


    private fun extractTiles(bitmap: Bitmap) {
        val columns = scaledBitmap.width / tileWidth
        val rows = scaledBitmap.height / tileHeight

        for (i in 0 until columns) {
            for (j in 0 until rows) {
                val x = i * tileWidth
                val y = j * tileHeight

                // Farbe im Zentrum einer Kachel
                val color = bitmap.getPixel(x + tileWidth / 2, y + tileHeight / 2)
                val tileObject = Object(Position(x.toFloat(), y.toFloat()), Size(tileWidth.toFloat(), tileHeight.toFloat()))

                if (tileMap[color] == null) {
                    tileMap[color] = mutableListOf(tileObject)
                } else {
                    tileMap[color]?.add(tileObject)
                }
            }
        }
    }

    private fun getTilesByColor(targetColor: Int): List<Object> {
        tileMap[targetColor]?. let {
            return it
        }

        return listOf()
    }

    fun getRandomTile(tiles: List<Object>): Object {
        val randomIndex = Random.nextInt(tiles.size)
        return tiles[randomIndex]
    }

    fun getWalls(): List<Object> = getTilesByColor(Color.BLACK) // Schwarz
    fun getKeys(): List<Object> = getTilesByColor(Color.rgb(255,255,0)) // Gelb
    fun getDoors(): List<Object> = getTilesByColor(Color.rgb(0,255,0)) // Grün
    fun getPlayerspawns(): List<Object> = getTilesByColor(Color.rgb(0,0,255)) // Blau
    fun getEnemyspawns(): List<Object> = getTilesByColor(Color.rgb(255,0,0)) // Rot
    fun getBushspawns(): List<Object> = getTilesByColor(Color.rgb(91,127,0)) // Buschgrün
    fun getTrapspawns(): List<Object> = getTilesByColor(Color.rgb(127,51,0)) // Braun
    fun getEyespawns(): List<Object> = getTilesByColor(Color.rgb(127,255,255)) // Hellblau
}