package com.hollowmaze.game.controller.helper

import StorageHelper
import android.annotation.SuppressLint
import android.content.Context
import com.hollowmaze.game.R

data class Level(val id: Int, val image: Int, var accessible: Boolean, val name : String, var completionTime: Float = 0f, var averageCommunityCompletionTime: String = "10 Sekunden")

@SuppressLint("StaticFieldLeak")
object LevelHelper {
    // Definition der Level-Informationenpü
    var levels = ArrayList<Level>()
    lateinit var currentLevel: Level
    private var startTimeMillis: Long = 0
    private var pausedTimeMillis: Long = 0
    private var isRunning: Boolean = false

    init {
        //levels.add(Level(1, R.drawable.test, true, "Test Level", averageCommunityCompletionTime = "10 seconds"))
        levels.add(Level(1, R.drawable.map_level_1, true, "First level", averageCommunityCompletionTime = "20 seconds"))
        levels.add(Level(2, R.drawable.map_level_2, false, "Second level", averageCommunityCompletionTime = "5 minutes"))
        levels.add(Level(3, R.drawable.map_level_3, false, "Third level", averageCommunityCompletionTime = "5 minutes"))
        levels.add(Level(4, R.drawable.map_level_4, false, "Fourth level", averageCommunityCompletionTime = "10 minutes"))
        levels.add(Level(5, R.drawable.map_level_5, false, "Fifth level", averageCommunityCompletionTime = "13 minutes"))
        levels.add(Level(6, R.drawable.map_level_6, false, "Sixth level", averageCommunityCompletionTime = "15 minutes"))
        levels.add(Level(7, R.drawable.map_level_7, false, "Seventh level", averageCommunityCompletionTime = "22 minutes"))

        currentLevel = levels[0]
    }

    // Funktion, um alle Level als Array zurückzugeben
    fun getAllLevels(): ArrayList<Level> {
        return levels
    }

    fun setAccessibilityOfNextLevel(context: Context) {
        setCompletionTime()
        getLevelById(currentLevel.id + 1)?.accessible = true
        StorageHelper.setLevels(getAllLevels(), context)
        setAllLevels(context)
    }

    fun updateCurrentLevel(dataItem: Level, context: Context) {
        currentLevel = dataItem
        StorageHelper.storeCurrentLevel(context, currentLevel)
    }

    fun getLevelById(tempId: Int): Level? {
        val levels = getAllLevels()
        return levels.find { it.id == tempId }
    }

    fun setAllLevels(context: Context) {
        currentLevel = StorageHelper.getCurrentLevel(context) ?: levels[0]
        levels = StorageHelper.getLevels(context)?: getAllLevels()
    }

    fun setCompletionTime() {
        stopLevelTimer()
        currentLevel.completionTime = getElapsedTime()
        resetLevelTimer()
    }
    fun startLevelTimer() {
        if (!isRunning) {
            startTimeMillis = System.currentTimeMillis()
            isRunning = true
        }
    }

    fun stopLevelTimer() {
        if (isRunning) {
            pausedTimeMillis += System.currentTimeMillis() - startTimeMillis
            isRunning = false
        }
    }

    fun resetLevelTimer() {
        startTimeMillis = 0
        pausedTimeMillis = 0
        isRunning = false
    }

    fun getElapsedTime(): Float {
        return if (isRunning) {
            (System.currentTimeMillis() - startTimeMillis + pausedTimeMillis).toFloat()
        } else {
            pausedTimeMillis.toFloat()
        }
    }
}

