package com.hollowmaze.game.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.hollowmaze.game.R


class PauseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Action-Bar ausblenden
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pausescreen)
    }
    fun restart(view: View) {
        // restart the game
        val intent = Intent(this, LoadingscreenActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun goToLevelselect(view: View?) {
        val intent = Intent(this, LevelselectionActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun goToSettings(view: View?) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun goToMainMenu(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun resumeGame(view: View) {
        finish()
    }
}