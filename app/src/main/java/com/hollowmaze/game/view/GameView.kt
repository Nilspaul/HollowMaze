package com.hollowmaze.game.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.hollowmaze.game.R
import com.hollowmaze.game.model.GameModel
import com.hollowmaze.game.controller.activities.PauseActivity
import com.hollowmaze.game.controller.cooldown.Cooldown
import com.hollowmaze.game.controller.helper.BitmapExtract
import com.hollowmaze.game.controller.items.Eye
import com.hollowmaze.game.controller.helper.SettingsHelper
import com.hollowmaze.game.model.player.PlayerModel
import kotlin.math.*


class GameView(context: Context) : View(context) {
    var gameModel: GameModel = GameModel()
    val viewRenderer: ViewRenderer = ViewRenderer(context)
    val debug = mapOf<String, Boolean>(
        "tiles" to false,
        "fps" to true,
    )

    init {
        layoutParams = ViewGroup.LayoutParams(
            gameModel.canvasSize.width.toInt(),
            gameModel.canvasSize.height.toInt()
        )
    }

    /**
     * Aktualisiert die GameView
     */
    fun update() {
        updateWindow(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
    }

    /**
     * Event-Listener: Wenn die Displaygröße geändert wird (z.B. Rotation)
     */
    override fun onSizeChanged(w: Int, h: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(w, h, oldWidth, oldHeight)

        // Display/Window-Size speichern
        updateWindow(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
    }

    /**
     * Aktualisiert, die Window-Sizes
     */
    fun updateWindow(w: Int, h: Int) {
        gameModel.window.width = w.toFloat()
        gameModel.window.height = h.toFloat()
    }


    /**
     * Event-Listener: Canvas-Draw
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Canvas um Viewport verschieben
        canvas.translate(-gameModel.viewport.x, -gameModel.viewport.y)

        // ViewRenderer Canvas setzen
        viewRenderer.canvas = canvas

        // Update-ViewRenderer-Settings
        viewRenderer.update(gameModel)

        /**
         * Drawing
         */
        // Untergrund zeichnen
        drawCanvasUnderground(canvas)

        // Canvas verdunkeln
        drawCanvasOverlay(canvas)

        // Vordergrund zeichnen
        drawCanvasForeground(canvas)

    }


    /**
     * DEBUG: FPS in der oberen rechten Ecke des Viewports zeichnen
     */
    private var frameCounter = 0
    private var lastTimeChecked: Long = 0
    private var fps = 0
    fun showFPS(canvas: Canvas) {
        frameCounter++
        val currentTime = System.currentTimeMillis()

        // Refresh-Rate (Einmal pro Sekunde)
        if (currentTime - lastTimeChecked >= 1000) {
            fps = frameCounter
            frameCounter = 0
            lastTimeChecked = currentTime
        }

        val fpsStyle = Paint().apply {
            color = Color.argb(255, 227, 61, 148)
            textSize = 50f
        }

        // FPS anzeigen
        val fpsText = "FPS: $fps"
        val textWidth = fpsStyle.measureText(fpsText)
        val paddingX = 20
        fpsStyle.setShadowLayer(15.0f, 0.0f, 0.0f, Color.WHITE);
        canvas.drawText(fpsText, gameModel.viewport.x + gameModel.window.width - textWidth - paddingX, gameModel.viewport.y + 60f, fpsStyle)
    }



    private val floorBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.maze_stone_tile0)
    private val floorResized = Bitmap.createScaledBitmap(floorBitmap, BitmapExtract.tileSize.toInt(), BitmapExtract.tileSize.toInt(), true)
    private val floorShader: Shader = BitmapShader(floorResized, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    private val floorPaint = Paint().apply {
        shader = floorShader
    }

    /**
     * Zeichne den Hintergrund des Canvas
     */
    fun drawCanvasUnderground(canvas: Canvas) {

        // Base-Canvas
        canvas.drawColor(Color.rgb(255, 255,255))
        canvas.drawRect(0f, 0f,
            gameModel.canvasSize.width, gameModel.canvasSize.height, floorPaint)

        viewRenderer.drawBackground(canvas)

        /**
         * Debug-Tiles
         */
        debug["tiles"]?.takeIf { it }?.let {
            showDebugTiles(canvas, 200f)
        }

        //Draw traps
        for(trap in gameModel.traps) {
            trap.draw(canvas)
        }

        //Draw interaktive objects
        gameModel.door?.draw(canvas)

        //Draw eyes
        for (item in gameModel.items) {
            if(!item.isPickedUp) {
                item.draw(canvas, gameModel)
            }
        }

        // Player
        gameModel.player?.draw(canvas)

        //Draw bushes
        for(bush in gameModel.bushes) {
            bush.draw(canvas)
        }

        // Enemy
        gameModel.enemy?.draw(canvas)

        //Draw key in background when it is not picked up
        if(gameModel.key?.isPickedUp == false) {
            gameModel.key?.draw(canvas)
        }

        if(gameModel.player.model.hasFiredPing && !gameModel.player.model.hasUnlimitedPings) {
            gameModel.cooldown.drawClockwiseFillCircle(canvas, gameModel.player.model.position.x + 150f,gameModel.player.model.position.y - 80f, 25f, gameModel, gameModel.pingController.pings?.firstOrNull())
        }


    }

    /**
     * Zeichne den Vordergrund des Canvas
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun drawCanvasForeground(canvas: Canvas) {

        viewRenderer.drawForeground(canvas)

        // Controller
        gameModel.control?.draw(canvas)

        // Pings
        gameModel.pingController.draw(canvas)

        // NoiseBorder
        gameModel.directionBorderController?.let {
            it.draw(canvas)
        }

        // CollisionsWarnung
        gameModel.collisionWarning?.let {
            it.draw(canvas)
        }

        // Pause-Button
        gameModel.pauseButton?.draw(canvas)

        /**
         * FPS-Anzeige
         */
        debug["fps"]?.takeIf { it }?.let {
            showFPS(canvas)
        }

        // Aufgenommenen Key
        if (gameModel.key?.isPickedUp == true) {
            gameModel.key?.draw(canvas)
        }

        // Innerhalb der Schleife für jedes aufgehobene Item zeichnen
        for (item in gameModel.items) {
            if (item.isPickedUp ) {
                item.draw(canvas, gameModel)
                gameModel.cooldown.drawClockwiseFillCircle(canvas, gameModel.viewport.x + 340f, gameModel.viewport.y + gameModel.window.height/1.1f, 25f, gameModel, item)
                break
            }
        }
    }

    /**
     * Verdunkelt das Canvas und hebt nur einen gewissen Bereich hervor
     */
    fun drawCanvasOverlay(canvas: Canvas) {

        // Speichern Sie den aktuellen Zustand des Canvas
        val fullCanvasRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val saveCount = canvas.saveLayer(fullCanvasRect, null)

        // Fläche Verdunkeln
        canvas.drawColor(Color.argb(255, 20, 20, 20))

        val radius = gameModel.player.model.size.width/2 + SettingsHelper.currentDifficulty.lightRadius
        // Für den radialen Verlauf
        var gradientPaint = Paint()
        gradientPaint.shader = RadialGradient(
            gameModel.player.model.position.x + gameModel.player.model.size.centerX,
            gameModel.player.model.position.y + gameModel.player.model.size.centerY,
            radius,
            intArrayOf(Color.BLACK, Color.argb(250, 0, 0, 0), Color.argb(10, 0, 0, 0)),
            floatArrayOf(0f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )

        // PorterDuff-Modus für ausstanzen
        gradientPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

        // Circle mit Verlauf um Spieler zeichnen
        canvas.drawCircle(gameModel.player.model.position.x + gameModel.player.model.size.centerX, gameModel.player.model.position.y + gameModel.player.model.size.centerY, radius, gradientPaint)


        // Ping zeichnen
        gameModel.pingController.pings?.dropLast(1)?.forEach { ping ->
            var pingPaint = Paint()
            val pingRadius = 150f
            val width = pingRadius
            val height = width * 0.75f

            pingPaint.shader = RadialGradient(
                ping.endPos.x,
                ping.endPos.y + height,
                radius,
                intArrayOf(Color.BLACK, Color.argb(200, 0, 0, 0), Color.argb(10, 0, 0, 0)),
                floatArrayOf(0f, 0.4f, 1f),
                Shader.TileMode.CLAMP
            )
            pingPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            val ovalBounds = RectF(
                ping.endPos.x - width,
                ping.endPos.y - height,
                ping.endPos.x + width,
                ping.endPos.y + height
            )

            // Zeichnen Sie das Oval
            canvas.drawOval(ovalBounds, pingPaint)
        }

        // original-Canvas sicherheitshalber wiederherstellen und PorterDuff-Modus deaktivieren
        gradientPaint.xfermode = null
        canvas.restoreToCount(saveCount)
    }

    /**
     * DEBUG: Zeichnet ein Kachelmuster mit Koordinaten zur Orientierung
     */
    fun showDebugTiles(canvas: Canvas, tileSize: Float = 200f) {
        val tilePaint = Paint().apply {
            color = Color.GRAY
            style = Paint.Style.STROKE
        }
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 25f
            isAntiAlias = true
        }

        // Zeichnen des Kachelmusters
        for (x in 0 until gameModel.canvasSize.width.toInt() step tileSize.toInt()) {
            for (y in 0 until gameModel.canvasSize.height.toInt() step tileSize.toInt()) {
                canvas.drawRect(x.toFloat(), y.toFloat(), x + tileSize, y + tileSize, tilePaint)
                canvas.drawText(
                    "(X:${x})",
                    x.toFloat(),
                    y.toFloat() - tileSize.toInt() + 30,
                    textPaint
                )
                canvas.drawText(
                    "(Y:${y})",
                    x.toFloat() + (tileSize.toInt() / 2),
                    y.toFloat(),
                    textPaint
                )
            }
        }
    }


    /**
     * Prüft, ob ein Spieler sich in der Nähe einer Viewport-Border befindet
     * TODO: Aufteilen in getDistance2ViewportBorder und updateViewport
     */
    fun isPlayerNearViewportBorder(player: PlayerModel): Boolean {
        val tolerance_horizontal = floor(gameModel.window.width / 3.5.toFloat())
        val tolerance_vertical = floor(gameModel.window.height / 3.5.toFloat())

        var newX: Float? = null
        var newY: Float? = null
        var updated = false

        // Player an linken Kante des Viewports
        if (player.position.x - gameModel.viewport.x < tolerance_horizontal) {
            newX = player.position.x - tolerance_horizontal
        }
        // Player an rechten Kante des Viewports
        else if (gameModel.viewport.x + gameModel.window.width - (player.position.x + player.size.width + tolerance_horizontal) < 0) {
            newX = player.position.x + player.size.width - gameModel.window.width + tolerance_horizontal
        }

        // Player an oberen Kante des Viewports
        if (player.position.y - gameModel.viewport.y < tolerance_vertical) {
            newY = player.position.y - tolerance_vertical
        }
        // Player an unteren Kante des Viewports
        else if (gameModel.viewport.y + gameModel.window.height - (player.position.y + player.size.height + tolerance_vertical) < 0) {
            newY = player.position.y + player.size.height - gameModel.window.height + tolerance_vertical
        }

        newX?.let {
            gameModel.viewport.x = max(0f, min(it, gameModel.canvasSize.width - gameModel.window.width))
            updated = true
        }

        newY?.let {
            gameModel.viewport.y = max(0f, min(it, gameModel.canvasSize.height - gameModel.window.height))
            updated = true
        }

        return updated
    }

    /**
     * TODO
     * Liefert die Entfernung von Koordinaten zur Viewport-Border
     */
    fun getDistance2ViewportBorder(x: Float, y: Float) {

    }

    /**
     * Aktualisiert die Viewport-Position
     */
    fun updateViewport(x: Float, y: Float) {
        setViewportPosition(x, y)
        Log.v("update_ViewportPosition", "${x} - ${y}")
    }

    /**
     * Setzt die Viewport-Position (Top-Left) per X/Y-Koordinaten
     */
    private fun setViewportPosition(x: Float, y: Float) {
        gameModel.viewport.x = x
        gameModel.viewport.y = y
    }

    /**
     * kleiner Hack, sonst meckert die IDE
     * TODO: ggf. ablösen
     */
    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun checkIfPauseButtonClicked(event: MotionEvent): Boolean? {
        gameModel.pauseButton?.let { btn ->
            if (btn.onClick(event)) {
                val intent = Intent(context, PauseActivity::class.java)
                context.startActivity(intent)

                return true
            }
        }
        return null
    }
}