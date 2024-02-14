package com.hollowmaze.game.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hollowmaze.game.controller.adapters.Leveladapter
import com.hollowmaze.game.controller.helper.Level
import com.hollowmaze.game.controller.helper.LevelHelper
import com.hollowmaze.game.databinding.LevelselectBinding

class LevelselectionActivity : AppCompatActivity() {

    private lateinit var binding: LevelselectBinding
    private lateinit var mList: ArrayList<Level>
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Action-Bar ausblenden
        supportActionBar?.hide()
        binding = LevelselectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        mList = ArrayList()
        prepareData()

        val adapter = Leveladapter(mList, this )
        binding.recyclerView.adapter = adapter
    }
    private fun prepareData() {
        mList.addAll(LevelHelper.getAllLevels())
    }

    fun goToMainMenu(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}