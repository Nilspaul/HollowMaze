package com.hollowmaze.game.controller.helper

import StorageHelper
import android.content.Context
import kotlin.properties.Delegates

data class Difficulty(
    val name: String,
    val lightRadius: Float,
    val movementSpeed: Float,
    val itemDuration: Float,
    val pingCooldown: Float,
    val id: Int,
    val enemyMovmentSize: Float
)
object SettingsHelper {
    var difficulties = ArrayList<Difficulty>()
    lateinit var currentDifficulty: Difficulty
    var vibrationStatus by Delegates.notNull<Boolean>()
    init {
        difficulties.add(Difficulty("Easy", 250f, 15f, 8000f, 2000f, 1, 5f))
        difficulties.add(Difficulty("Medium", 200f, 15f, 4000f, 4000f,2, 10f))
        difficulties.add(Difficulty("Hard", 100f, 15f, 2000f, 8000f,3, 20f))
    }
    fun setSettings(context: Context) {
        val index = StorageHelper.getSelectedDifficulty(context)
        if (index!= -1) {
            currentDifficulty = difficulties[index]
        } else {
            currentDifficulty = difficulties[0]
            StorageHelper.setSelectedDifficulty(context, 0)
        }

        vibrationStatus = if (StorageHelper.getVibrationStatus(context) !== false) {
            StorageHelper.getVibrationStatus(context)
        } else {
            false
        }
    }
}

