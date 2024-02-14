package com.hollowmaze.game.controller.activities

import StorageHelper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hollowmaze.game.R
import com.hollowmaze.game.controller.helper.SettingsHelper
import com.hollowmaze.game.controller.helper.SettingsHelper.difficulties

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Action-Bar ausblenden
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val difficulty = findViewById<TextView>(R.id.difficulty)
        difficulty.text = SettingsHelper.currentDifficulty.name
        val vibration = findViewById<Switch>(R.id.vibration)
        vibration.isChecked = SettingsHelper.vibrationStatus
    }
    fun goToMainMenu(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun resetState(view: View?) {
        StorageHelper.clearPreferences(this)
        Toast.makeText(this, "Einstellungen wurden zurückgesetzt. Starte die App neu", Toast.LENGTH_LONG).show()
    }

    fun setNextDifficulty(view: View?) {
        val difficulty = findViewById<TextView>(R.id.difficulty)
        val index = difficulties.indexOfFirst { it.name == difficulty.text }
        if (index != -1 && index < difficulties.size - 1) {
            difficulty.text = difficulties[index + 1].name
            StorageHelper.setSelectedDifficulty(this, index + 1)
        } else {
            difficulty.text = difficulties[0].name // Zurück zum ersten Eintrag, wenn am Ende des Arrays
            StorageHelper.setSelectedDifficulty(this, 0)
        }
    }
    fun setLastDifficulty (view: View?) {
        val difficulty = findViewById<TextView>(R.id.difficulty)
        val index  = difficulties.indexOfFirst{it.name == difficulty.text}
        if(index != -1 && index > 0 ) {
            difficulty.text = difficulties[index - 1].name
            StorageHelper.setSelectedDifficulty(this, index - 1)
        } else {
            difficulty.text = difficulties[difficulties.size - 1].name
        }
    }
    fun setVibrationStatus (view: View?) {
        val status = !SettingsHelper.vibrationStatus
        StorageHelper.setVibrationStatus(this, status)
    }
}