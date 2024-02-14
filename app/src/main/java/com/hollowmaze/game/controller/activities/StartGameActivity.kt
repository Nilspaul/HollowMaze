package com.hollowmaze.game.controller.activities

import StorageHelper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.hollowmaze.game.controller.GameController
import com.hollowmaze.game.controller.helper.LevelHelper
import com.hollowmaze.game.model.GameModel
import com.hollowmaze.game.view.GameView

class StartGameActivity : AppCompatActivity() {

    private lateinit var model: GameModel
    private lateinit var view: GameView
    private lateinit var controller: GameController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Action-Bar ausblenden
        supportActionBar?.hide()

        // Init
        model = GameModel()
        view = GameView(this).apply {
            gameModel = this@StartGameActivity.model
        }
        setContentView(view)
        view.update()
        controller = GameController(model, view, this)

        // Touch-Events-Listener
        view.setOnTouchListener { _, event ->
            controller.handleTouch(event)
            true
        }
        controller.startGame()
    }

    override fun onStart() {
        Log.v("onStart", "start")
        super.onStart()
    }

    override fun onResume() {
        Log.v("pnResume", "resume")

        super.onResume()
        controller.startGameLoop()
    }

    override fun onPause() {
        super.onPause()
        Log.v("startgamePause", "pause")
        controller.stopGameLoop()
    }
    override fun onStop() {
        super.onStop()
        Log.v("startgamePause", "stop")
        controller.stopGameLoop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("startgamePause", "destroy")
        controller.stopGameLoop()
    }
}