package com.hollowmaze.game.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hollowmaze.game.R


class DeathscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Action-Bar ausblenden
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deathscreen)
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


}