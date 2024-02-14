package com.hollowmaze.game.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Looper
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import com.hollowmaze.game.controller.activities.LoadingscreenActivity
import com.hollowmaze.game.controller.bush.Bush
import com.hollowmaze.game.controller.controls.JoystickController
import com.hollowmaze.game.controller.door.Door
import com.hollowmaze.game.controller.helper.BitmapExtract
import com.hollowmaze.game.controller.items.Eye
import com.hollowmaze.game.controller.helper.Box
import com.hollowmaze.game.controller.helper.Level
import com.hollowmaze.game.controller.helper.LevelHelper
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.SettingsHelper
import com.hollowmaze.game.controller.helper.Size
import com.hollowmaze.game.controller.helper.VibrationHelper
import com.hollowmaze.game.controller.helper.simpleMath
import com.hollowmaze.game.controller.key.Key
import com.hollowmaze.game.controller.pausebutton.PauseButton
import com.hollowmaze.game.controller.player.BatController
import com.hollowmaze.game.controller.player.OwlController
import com.hollowmaze.game.controller.player.PlayerController
import com.hollowmaze.game.controller.trap.Trap
import com.hollowmaze.game.model.GameModel
import com.hollowmaze.game.model.MaterialModel
import com.hollowmaze.game.model.Ping
import com.hollowmaze.game.model.player.PlayerModel
import com.hollowmaze.game.model.Vector2
import com.hollowmaze.game.model.toVector
import com.hollowmaze.game.model.search.AStarAlgorithm
import com.hollowmaze.game.model.search.DFSAlgorithm
import com.hollowmaze.game.model.search.Maze
import com.hollowmaze.game.model.search.PathFinder
import com.hollowmaze.game.view.GameView
import com.hollowmaze.game.view.WallView
import kotlin.math.*
import kotlin.random.Random

class GameController(
    private val model: GameModel,
    private val view: GameView,
    val context: Context
) {
    val debug = mapOf<String, Boolean>(
        "maze" to false,
    )

    private val handler = Handler(Looper.getMainLooper())
    private val tickRate: Long = 1000.toLong() / 60
    private var lastFrameTime: Long = 0
    private val joystickController = JoystickController(view.context, model)
    private val pauseButton = PauseButton(view.context, model)
    private val key = Key(view.context, model)
    private val door = Door(view.context, model)
    private val directionBorderController = DirectionBorderController(model)
    private val collisionWarning = CollisionWarning(model)
    private var lastDrawTimeMillis: Long = 0

    private var enemySearchDFS: DFSAlgorithm? = null
    private var pathfinderDFS: PathFinder? = null
    private var enemySearchAStar: AStarAlgorithm? = null
    private var pathfinderAStar: PathFinder? = null
    private var searchMaze: Maze? = null

    val vibration: VibrationHelper = VibrationHelper(context)
    var lastCollision: Long = 0

    lateinit var player: PlayerModel
    var enemy: PlayerModel? = null
    var owl = OwlController(view.context, model)
    var bat = BatController(view.context, model)

    init {
        // Enemy anlegen
        model.enemy = owl
        enemy = (model.enemy as OwlController).model

        // PlayerController an GameModel-Player anhängen
        model.player = bat
        player = model.player.model


        model.control = joystickController

        model.pauseButton = pauseButton
        model.collisionWarning = collisionWarning

        model.key = key

        model.door = door

        model.directionBorderController = directionBorderController
    }

    /**
     * Gameloop, mit hinterlegter Tickrate, um das Game in X Ticks pro Sekunden zu aktualisieren
     */
    private val gameLoop = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastFrameTime) >= tickRate) {
                // Game-Updates abrufen
                updateGame()

                // komplette View neu zeichnen
                // TODO: Eigenen Renderer erstellen
                view.invalidate()

                // Last-Frame-Zeitpuntk speichern
                lastFrameTime = currentTime
            }

            // nächsten Tick definieren
            handler.postDelayed(this, tickRate)
        }
    }

    var lastEnemyMovment = 0L
    var isWarning = false

    /**
     * Aktualisiert das laufende Game und deren Inhalte
     */
    private fun updateGame() {

        // Spielerbewegungen
        // TODO: Auslagern in Player-Updates
        player?.moveDirection?.let { (deltaX, deltaY) ->
            val speedFactor = SettingsHelper.currentDifficulty.movementSpeed
            val originalX = player.position.x
            val originalY = player.position.y

            // Spieler prüfen & bewegen

            // Ausrichtung des Spielers festlegen
            player.setMovingDirection(deltaX, deltaY)

            // Temporäre Positionen
            var newX = player.position.x + deltaX * speedFactor
            var newY = player.position.y + deltaY * speedFactor


            val collObjects = player.getCollisions(model.walls)
            if (collObjects.isEmpty()) {
                player.addPreviousPosition(Position(originalX, originalY))
            } else if (System.currentTimeMillis() - collisionWarning.lastWarningTimer >
                collisionWarning.warningInterval
            ) {
                isWarning = true
            }

            for (collision in collObjects) {
                // Berechne die Überlappung in X- und Y-Richtung
                val overlapX = if (deltaX > 0) {
                    collision.boundingBox.left - player.boundingBox.right
                } else {
                    collision.boundingBox.right - player.boundingBox.left
                }

                val overlapY = if (deltaY > 0) {
                    collision.boundingBox.top - player.boundingBox.bottom
                } else {
                    collision.boundingBox.bottom - player.boundingBox.top
                }

                // Wende die Korrektur an
                if (abs(overlapX) < abs(overlapY)) {
                    newX += min(overlapX, 5f)
                } else {
                    newY += min(overlapY, 5f)
                }

                // Sonderlocke: Controller im Emulator außerhalb des Screen gezogen
                if (abs(player.position.x - newX) > 20 || abs(player.position.y - newY) > 20) {

                    newX = player.previousPositions.last().x
                    newY = player.previousPositions.last().y

                    Log.e("collision", "StuckinWall: ${player.position.toString()} | Fallback to Previous: ${player.previousPositions.last().toString()}")
                    player.updatePosition(newX, newY)

                    break
                }


                // Erkannte Kollision weiterverarbeiten
                if ((System.currentTimeMillis() - lastCollision > 1_500)) {

                    // Vibration auslösen
                    if (SettingsHelper.vibrationStatus) {
                        vibration.vibrate(500)
                    }

                    // A-Star-Algo ausführen
                    pathfinderAStar?.let { algo ->
                        debug["maze"]?.takeIf { it }?.let {
                            Log.v("maze", "Neue A-Star Pfadsuche starten")
                        }

                        val path = algo.getPath(
                            Pair(
                                simpleMath.roundUpToNearestHundred(enemy!!.position.x.toInt()),
                                simpleMath.roundUpToNearestHundred(enemy!!.position.y.toInt())
                            ),
                            Pair(
                                simpleMath.roundUpToNearestHundred(player.position.x.toInt()),
                                simpleMath.roundUpToNearestHundred(player.position.y.toInt())
                            )
                        )
                        model.enemy!!.model.searchPath = path.toMutableList()
                        debug["maze"]?.takeIf { it }?.let {
                            Log.v("maze", "AStar-Path: ${path}")
                        }

                        model.enemy!!.model.enemyMovmentSpeed = 0 // Wait pro Position in Millisekunden
                        model.enemy!!.model.enemyMovmentSize = 20f // Sprünge zwischen den Kacheln
                    }

                    lastCollision = System.currentTimeMillis()
                }

            }

            player.updatePosition(newX, newY)
        }

        //
        if(isWarning) warnPlayer()

        // Prüfe,ob der Spieler in einem Busch ist
        player.isHiddenInBush = false
        model.bushes.forEach { bush ->
            val distance = bush.center.distanceTo(player.center)
            if(distance <= player.playerCollisionTolerance) {
                player.isHiddenInBush = true
            }
        }

        // Prüfe, ob der Spieler eine Falle trifft
        model.traps.forEach { trap ->
            val distance = trap.center.distanceTo(player.center)
            if (distance <= player.playerCollisionTolerance) {
                triggerDeath()
            }
        }

        // Prüfe, ob der Spieler ein Augeaufgehoben hat
        model.eyes.forEach { eye ->
            model.items.add(eye)
            eye.collisionWithPlayer()
        }

        // Prüfen, ob der Gegner mit dem Spieler kollidiert
        enemy?.let { enemy ->
            if(!player.isHiddenInBush) {
                if (enemy.center.distanceTo(player.center) <= enemy.playerCollisionTolerance) {
                    triggerDeath()
                }
            }
        }

        model.pingController.pings.let { pings ->
            model.pingCollidesWithWalls()?.let {
                it.second?.let { collidedWall ->
                    val ping = pings.last()
                    val pingEndPosition = it.first.collisionPoint + ping.reflect(
                        it.first.edge.getNormal(collidedWall.center.toVector()),
                        it.first.collisionPoint
                    )
                    val pingStartPosition =
                        it.first.collisionPoint + (pingEndPosition - it.first.collisionPoint).normalize()
                    ping.endPos = it.first.collisionPoint
                    pings.add(Ping(pingStartPosition, pingEndPosition, System.currentTimeMillis()))
                    Log.v("collision", "collided3->${pings.size}")
                }
            }
        }

        model.directionBorderController?.collision = model.directionBorderController?.model?.getCollision()

        // Standard Enemy-Suche
        model.enemy?.model?.searchPath?.let { path ->
            if (System.currentTimeMillis() - lastEnemyMovment >= 0) {
                if (path.isNotEmpty()) {
                    val next = path.first()
                    val currentX = model.enemy!!.model.position.x
                    val currentY = model.enemy!!.model.position.y
                    val deltaX = next.first.toFloat() - currentX
                    val deltaY = next.second.toFloat() - currentY
                    val distance = sqrt(deltaX * deltaX + deltaY * deltaY)

                    enemy?.setMovingDirection(deltaX, deltaY)

                    if (distance < model.enemy!!.model.enemyMovmentSize) {
                        // Pathknoten entfernen und kurz warten
                        path.removeFirst()
                        lastEnemyMovment = System.currentTimeMillis() + model.enemy!!.model.enemyMovmentSpeed
                    } else {
                        // Weiter zum nächsten Punkt wandern
                        val moveX = model.enemy!!.model.enemyMovmentSize * (deltaX / distance)
                        val moveY = model.enemy!!.model.enemyMovmentSize * (deltaY / distance)
                        model.enemy!!.model.updatePosition(currentX + moveX, currentY + moveY)
                        lastEnemyMovment = System.currentTimeMillis()
                    }
                } else {
                    lastEnemyMovment = System.currentTimeMillis() + 500
                    debug["maze"]?.takeIf { it }?.let {
                        Log.v("maze", "Keine Pfadknoten mehr vorhanden")
                    }

                    pathfinderDFS?.let { algo ->
                        debug["maze"]?.takeIf { it }?.let {
                            Log.v("maze", "Neue Pfadsuche starten")
                        }

                        var playerPos = Pair(simpleMath.roundUpToNearestHundred(player.position.x.toInt()), simpleMath.roundUpToNearestHundred(player.position.y.toInt()))
                        val enemyPos = Pair(simpleMath.roundUpToNearestHundred(enemy!!.position.x.toInt()), simpleMath.roundUpToNearestHundred(enemy!!.position.y.toInt()))

                        // Falls der Player versteckt ist den Enemy im Bereich suchen lassen
                        if(player.isHiddenInBush) {
                            playerPos = Pair(simpleMath.roundUpToNearestHundred(player.position.x.toInt()  + (-200..200).random()), simpleMath.roundUpToNearestHundred(player.position.y.toInt()  + (-200..200).random()))
                        }
                        val path = algo.getPath(enemyPos, playerPos)

                        model.enemy!!.model.searchPath = path.toMutableList()
                        debug["maze"]?.takeIf { it }?.let {
                            Log.v("maze", "New DFS-Path: ${path}")
                        }

                        // Standard-Speed laden
                        model.enemy!!.model.enemyMovmentSpeed = model.enemy!!.model.enemyMovmentSpeedDefault
                        model.enemy!!.model.enemyMovmentSize = model.enemy!!.model.enemyMovmentSizeDefault
                    }
                }
            }
        }

        // Prüft, ob der Spieler am Rande des Viewport ist
        view.isPlayerNearViewportBorder(player)

        // Prüft, ob ein Key vom Spieler berührt wird
        key.collisionWithPlayer(player)

        // Prüft, ob eine Tür vom Spieler berühert wird
        door.collisionWithPlayer(player)
    }


    /**
     * Blended einen roten Bildschirm ein und wieder aus
     */
    private fun warnPlayer() {
        if (!collisionWarning.fadeInTimerIsSet && !collisionWarning.fadeOutTimerIsSet) {
            collisionWarning.fadeInTimerIsSet = true
            collisionWarning.fadeInTimer = System.currentTimeMillis()
        }
        if (collisionWarning.fadeInTimerIsSet) {
            val timePassed = System.currentTimeMillis() - collisionWarning.fadeInTimer
            Log.d("timepass", (timePassed).toString())
            if (timePassed >= collisionWarning.fadeTime) {
                collisionWarning.fadeOutTimerIsSet = true
                collisionWarning.fadeInTimerIsSet = false;
                collisionWarning.fadeOutTimer = System.currentTimeMillis()
            } else {
                collisionWarning.alphaValue = (timePassed / collisionWarning.fadeTime)
                Log.d("time1", (timePassed / collisionWarning.fadeTime).toString())
            }


            }
            Log.d("warning",collisionWarning.fadeOutTimerIsSet.toString())
            if (collisionWarning.fadeOutTimerIsSet) {
                val timePassed = System.currentTimeMillis() - collisionWarning.fadeOutTimer
                if (timePassed <= collisionWarning.fadeTime) {
                    Log.d("time2",timePassed.toString())
                    collisionWarning.alphaValue =
                        1f - (timePassed / collisionWarning.fadeTime)
                } else {
                    collisionWarning.fadeOutTimerIsSet = false
                    isWarning = false
                    collisionWarning.fadeInTimerIsSet = false
                    collisionWarning.lastWarningTimer = System.currentTimeMillis()
                }
                Log.d("warning",collisionWarning.fadeOutTimerIsSet.toString())
            }
    }

    /**
     * Startet das Spiel und initalisiert die grundlegenden Aufbau
     */
    fun startGame() {
        // GameLoop initalisieren
        startGameLoop()

        val lastClickedResourceId = LevelHelper.currentLevel.image

        // Map laden
        var mapData = BitmapExtract(
            BitmapFactory.decodeResource(
                view.context.resources,
                lastClickedResourceId
            ), view.gameModel.canvasSize
        );


        // Spieler platzieren
        var playerSpawns = mapData.getPlayerspawns()
        if (playerSpawns.size > 0) {
            Log.v("playerSpawns", "${playerSpawns}")
            val randomPlayerSpawn = mapData.getRandomTile(playerSpawns)
            player.updatePosition(randomPlayerSpawn.position.x, randomPlayerSpawn.position.y)
        } else {
            player.updatePosition(
                view.gameModel.canvasSize.centerX.toFloat(),
                view.gameModel.canvasSize.centerY.toFloat()
            )
        }

        // Viewport auf Spieler setzen - Clamped-Version, damit kein Canvas-Border gezeigt wird
        val clampedViewportX =
            (player.position.x - view.gameModel.window.width / 2 + player.size.width / 2).coerceIn(
                0f,
                view.gameModel.canvasSize.width - view.gameModel.window.width
            )
        val clampedViewportY =
            (player.position.y - view.gameModel.window.height / 2 + player.size.height / 2).coerceIn(
                0f,
                view.gameModel.canvasSize.height - view.gameModel.window.height
            )
        view.updateViewport(clampedViewportX, clampedViewportY)

        // Enemy platzieren
        var enemySpawns = mapData.getEnemyspawns()
        if (enemySpawns.size > 0) {
            val randomEnemySpawn = mapData.getRandomTile(enemySpawns)
            enemy?.updatePosition(randomEnemySpawn.position.x, randomEnemySpawn.position.y)
        } else {
            model.enemy = null
        }


        var keys = mapData.getKeys()
        if (keys.size > 0) {
            model.key?.position = Position(keys[0].position.x, keys[0].position.y)
        } else {
            model.key = null
        }


        var doors = mapData.getDoors()
        if (doors.size > 0) {
            model.door?.position = Position(doors[0].position.x, doors[0].position.y)
        } else {
            model.door = null
        }

        //Büsche auslesen und speichern
        var bushes = mapData.getBushspawns()
        for (value in bushes) {
            val bush = Bush(
                view.context,
                Position(value.position.x, value.position.y),
                Size(150f, 150f)
            )
            model.bushes.add(bush)
        }
        //Fallen auslesen und speichern
        var traps = mapData.getTrapspawns()
        for (value in traps) {
            val trap = Trap(
                view.context,
                Position(value.position.x, value.position.y),
                Size(150f, 150f)
            )
            model.traps.add(trap)
        }

        //Augen auslesen und speichern
        var eyes = mapData.getEyespawns()
        for (value in eyes) {
            val eye = Eye(
                view.context,
                Position(value.position.x, value.position.y),
                Size(150f, 150f),
                model
            )
            model.eyes.add(eye)
        }

        var loaded_walls = mapData.getWalls()
        // Walls speichern
        loaded_walls?.forEach {
            model?.walls?.add(
                MaterialModel(
                    Position(it.position.x, it.position.y),
                    Size(it.size.width, it.size.height),
                    color = Color.argb(
                        255,
                        Random.nextInt(50, 255),
                        Random.nextInt(50, 255),
                        Random.nextInt(50, 255)
                    )
                )
            )
        }

        // Walls-Ausrichtung definieren
        val wallView = WallView(context, mapData.tileSize.toFloat(), mapData.tileSize.toFloat())
        model?.walls?.forEach { wall ->
            wall.orientation =
                wall.getObjectOrientation(wall, model.walls, mapData.tileSize.toFloat())

            wallView?.drawableRefs?.get(wall.orientation.toString())?.let {
                wall.updateRefBitmap(it)
            }
        }

        // Walls an Renderer übergeben
        model?.walls?.forEach { wall ->
            var bitmap = wallView?.scaledBitmap?.get(wall.bitmapRef.toString())
            bitmap?.let { Box(wall.position, wall.size, wall.paint, it) }
                ?.let { view.viewRenderer.add(it) }
        }


        // Walls an Suchalgo übertragen
        model?.enemy?.let {
            // Setzen Sie hier die Wände in Ihrem Labyrinth

            val e = enemy
            val p = player

            if (e != null && p != null) {
                searchMaze = Maze(model.canvasSize.width.toInt(), model.canvasSize.height.toInt())

                // Wände reinladen
                model.walls.forEach {
                    searchMaze!!.addWall(it.position.x.toInt(), it.position.y.toInt())
                }

                enemySearchDFS = DFSAlgorithm(searchMaze!!)
                pathfinderDFS = PathFinder(enemySearchDFS!!)
                val localPathfinderDFS = pathfinderDFS
                if (localPathfinderDFS is PathFinder) {
                    val path = localPathfinderDFS.getPath(
                        Pair(
                            e.position.x.toInt(),
                            e.position.y.toInt()
                        ), Pair(p.position.x.toInt(), p.position.y.toInt())
                    )
                    model.enemy!!.model.searchPath = path.toMutableList()

                    debug["maze"]?.takeIf { it }?.let {
                        Log.v("maze", "DFS: ${path}")
                    }
                }

                enemySearchAStar = AStarAlgorithm(searchMaze!!)
                pathfinderAStar = PathFinder(enemySearchAStar!!)
            }
        }
        LevelHelper.startLevelTimer()
    }

    /**
     * Startet den Gameloop
     */
    fun startGameLoop() {
        handler.post(gameLoop)
    }

    /**
     * Stoppt den Gameloop
     */
    fun stopGameLoop() {
        handler.removeCallbacks(gameLoop)
    }


    /**
     * Verarbeitet Nutzer-Interaktionen
     */

    private var touchStartTime = 0L
    private val LONG_PRESS_THRESHOLD = 300 // 300ms für langes Drücken

    fun handleTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartTime = System.currentTimeMillis()
                joystickController.startTouch(event)
            }

            MotionEvent.ACTION_MOVE -> {
                action_move(event)
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                if (isSingleTap()) {
                    handleTap(event)
                }
                action_detached(event)
            }

            MotionEvent.ACTION_OUTSIDE -> {
                action_detached(event)
            }
        }
    }

    private fun isSingleTap(): Boolean {
        val touchEndTime = System.currentTimeMillis()
        return touchEndTime - touchStartTime < LONG_PRESS_THRESHOLD
    }

    /**
     * Verarbeitet einen Tap
     */
    fun action_down(event: MotionEvent) {
        handleTap(event)
        joystickController.startTouch(event)
    }


    /**
     * Verarbeitet, wenn der Tap "losgelassen" wird
     */
    fun action_detached(event: MotionEvent) {
        joystickController.stopTouch()
        player.moveDirection = null
    }

    /**
     * Verarbeitet, wenn der Tap gezogen wird "Drag and Drop"
     */
    fun action_move(event: MotionEvent) {
        if (!joystickController.disable) {
            player.moveDirection = joystickController.model.getDirection(event.x, event.y)
        }
    }


    /**
     * Verarbeitet einzelne Taps auf den Screen
     */
    private var lastTapTime = 0L
    private var tapCount = 0
    fun handleTap(event: MotionEvent) {
        val currentTime = System.currentTimeMillis()
        val interval = 300 // alles darunter zählt zu "einem tap"

        // Overlay-Buttons
        view.checkIfPauseButtonClicked(event)?.let {
            return
        }

        if (currentTime - lastTapTime <= interval) {
            tapCount++
            if (tapCount == 3) {
                tmp_action_setplayer(event)
                tapCount = 0
                return
            }
        } else {
            tapCount++
            // Single-Tap
            action_ping(event)
        }

        lastTapTime = currentTime
    }

    fun triggerDeath() {
        // Starte den Loadingscreen into Deathscreen
        val intent = Intent(context, LoadingscreenActivity::class.java)
        // Füge Acitvity (Daten) zum Intent hinzu
        intent.putExtra("ActivityToTrigger", "DeathscreenActivity")
        context.startActivity(intent)
        (context as Activity).finish()
    }

    fun action_ping(event: MotionEvent) {
        val currentTimeMillis = System.currentTimeMillis()

        // Überprüfe, ob die Zeit seit dem letzten Aufruf größer oder gleich dem Intervall ist
        if (model.cooldown.getActiveCooldowns().size == 0 || model.player.model.hasUnlimitedPings) {
            model.player.model.hasFiredPing = true
            lastDrawTimeMillis = currentTimeMillis

            if (!model.player.model.hasUnlimitedPings) {
                model.pingController.pings.clear()
            }

            val ping = Ping(
                (model.player as PlayerController).model.center.toVector(),
                Vector2(model.viewport.x + event.x, model.viewport.y + event.y),
                System.currentTimeMillis()
            )
            model.pingController.pings.add(ping)
            model.cooldown.addCooldown(model, ping)
        }


        if(!model.player.model.hasFiredPing && !model.player.model.hasUnlimitedPings) {
            model.cooldown.removeAllCooldowns()
        }

        // Hotfix: Gestuckte Pings entfernen
        if(!model.player.model.hasUnlimitedPings) {
            if(model.cooldown.getActiveCooldowns().size > 0) {
                if((model.cooldown.getActiveCooldowns().first().startTime + model.cooldown.cooldownDuration.toInt()) < currentTimeMillis ) {
                    model.cooldown.removeAllCooldowns()
                }
            }
        }

    }

    /**
     * TEST: Setzt den Player auf eine gewählte Position
     */
    fun tmp_action_setplayer(event: MotionEvent) {
        player.updatePosition(
            model.viewport.x + event.x - player.size.width / 2,
            model.viewport.y + event.y - player.size.height / 2
        )
    }
}
