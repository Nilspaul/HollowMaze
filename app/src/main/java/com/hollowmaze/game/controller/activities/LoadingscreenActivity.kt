package com.hollowmaze.game.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hollowmaze.game.R
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView


class LoadingscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Action-Bar ausblenden
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loadingscreeen)
        // Verzögerung von 5 Sekunden (5000 Millisekunden)
        val delayMillis = 4000
        //Nimm die Activity aus Intent entgegen
        val activityToTrigger = intent.getStringExtra("ActivityToTrigger")
        //Tipps für den Loadingscreen
        val sentences = arrayOf(
            "Be mindful of your movements! If you touch the wall, the enemy will know your location.",
            "Find the key to open the door to the next level.",
            "Send out pings to make your surroundings visible.",
            "Use bushes to hide from the enemy!",
            "Stay away from enemies and traps!",
            "You can adjust the difficulty level if it's too hard or too easy for you.",
            "You can adjust your movement speed by pulling the joystick more or less strongly.",
            "Through the level selection, you can also replay previous levels."
        )

        // Zufälligen Satz auswählen
        val randomSentence = sentences.random()

        // Text in eine TextView im Layout einfügen
        val tipsTextView = findViewById<TextView>(R.id.tips)
        tipsTextView.text = randomSentence
        Handler().postDelayed({
            // Hier wird der Code nach der Verzögerung von 5 Sekunden ausgeführt
            if(activityToTrigger == "DeathscreenActivity") {
                // Erstelle und zeige die neue Ansicht
                val intent = Intent(this, DeathscreenActivity::class.java)
                startActivity(intent)
                finish() // Schließt die aktuelle Ansicht
            } else {
                val intent = Intent(this, StartGameActivity::class.java)
                startActivity(intent)
                finish() // Schließt die aktuelle Ansicht
            }
        }, delayMillis.toLong())

        //Lade Animation

        val zoom: ImageView = findViewById(R.id.imageView4)
        val zoomAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.loading)
        zoom.startAnimation(zoomAnimation)


    }
}