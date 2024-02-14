import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.hollowmaze.game.controller.helper.Level
import com.hollowmaze.game.controller.helper.SettingsHelper

object StorageHelper {
    private const val PREFS_NAME = "MyPrefs" // Der Name deiner SharedPreferences-Datei
    private const val LEVELS_KEY = "levels"
    fun storeCurrentLevel(context: Context, currentLevel: Level) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val levelJson = gson.toJson(currentLevel)

        editor.putString("currentLevel", levelJson)

        // Änderungen speichern
        editor.apply()
    }

    fun getCurrentLevel(context: Context): Level? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val levelJson = sharedPreferences.getString("currentLevel", null)

        if (levelJson != null) {
            val gson = Gson()
            return gson.fromJson(levelJson, Level::class.java)
        }

        return null
    }
    fun setLevels(levels: ArrayList<Level>, context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val levelsJson = gson.toJson(levels)

        editor.putString(LEVELS_KEY, levelsJson)

        // Änderungen speichern
        editor.apply()
    }

    fun getLevels(context: Context): ArrayList<Level>? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val levelsJson = sharedPreferences.getString(LEVELS_KEY, null)

        if (levelsJson != null) {
            val gson = Gson()
            val levelArray: Array<Level> = gson.fromJson(levelsJson, Array<Level>::class.java)
            return ArrayList(levelArray.toList())
        }

        return null
    }

    fun clearPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.clear() // Löscht alle gespeicherten Daten in den SharedPreferences
        editor.apply() // Anwenden der Änderungen

    }
    fun setSelectedDifficulty(context: Context, index: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val difficultyJson = gson.toJson(SettingsHelper.difficulties[index])

        // Speichern Sie das serialisierte JSON-Objekt
        editor.putString("selectedDifficulty", difficultyJson)
        editor.putInt("selectedDifficultyIndex", index)
        editor.apply()
    }

    fun getSelectedDifficulty(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val index = sharedPreferences.getInt("selectedDifficultyIndex", -1)

        return index
    }
    fun setVibrationStatus(context: Context, status : Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Speicher Wert in den SharedPreferences
        editor.putBoolean("selectedVibrationStatus", status)
        editor.apply()
    }

    fun getVibrationStatus (context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val status = sharedPreferences.getBoolean("selectedVibrationStatus", false)

        return status
    }
}
