package com.hollowmaze.game.controller.cooldown

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.hollowmaze.game.controller.helper.SettingsHelper
import com.hollowmaze.game.controller.items.ItemController
import com.hollowmaze.game.model.GameModel

class Cooldown() {
    /**
     * Active Cooldowns speichern
     */
    companion object {
        val activeCooldowns: MutableList<CooldownInstance> = mutableListOf()
    }

    private val circleBounds = RectF()
    var currentSweepAngle = 360f
    var cooldownDuration = SettingsHelper.currentDifficulty.pingCooldown // Cooldown-Dauer in Millisekunden
    val startAngle = 0f

    /**
     * Style des Cooldowns
     */
    private val circlePaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    /**
     * Instance um Cooldowns zu verwalten
     */
    class CooldownInstance(
        var startTime: Long,
        val item: Any?,
    ) {
        override fun toString(): String {
            return "Type: ${item}; StartTime ${startTime}"
        }
    }

    /**
     * Fügt Cooldown der aktiven Cooldowns hinzu
     */
    fun addCooldown(model: GameModel, item: Any?) {
        activeCooldowns.add(CooldownInstance(System.currentTimeMillis(), item))
    }

    /**
     * Liefert alle aktiven Cooldowns
     */
    fun getActiveCooldowns(): MutableList<CooldownInstance> {
        return activeCooldowns
    }

    fun getActiveCooldownsByType(type: Any?): MutableList<CooldownInstance> {
        return if (type != null) {
            activeCooldowns.filter { cooldown ->
                cooldown.item?.javaClass == type.javaClass
            }.toMutableList()
        } else {
            mutableListOf() // was leeres liefern!
        }
    }

    /**
     * Entfernt ein Cooldown
     */
    fun removeCooldown(item: Any?) {
        activeCooldowns.removeIf { cooldownInstance ->
            cooldownInstance.item == item
        }
    }

    /**
     * Entfernt alle Cooldowns
     */
    fun removeAllCooldowns() {
        activeCooldowns.clear()
    }

    /**
     * Entfernt Cooldowns anhand seines Types
     */
    fun removeCooldownsByType(type: Any?) {
        if (type != null) {
            activeCooldowns.removeIf { cooldown ->
                cooldown.item?.javaClass == type.javaClass
            }
        }
    }

    fun drawClockwiseFillCircle(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        model: GameModel,
        item: Any?
    ) {

        val currentTime = System.currentTimeMillis()
        var draw = false

        val cooldown = activeCooldowns.find { it.item == item }
        if(cooldown != null) {
            var duration = cooldownDuration
            if(model.player.model.hasUnlimitedPings) {
                duration = SettingsHelper.currentDifficulty.itemDuration
            }

            val elapsedTime = currentTime - cooldown.startTime
            val progress = elapsedTime.toFloat() / duration
            currentSweepAngle = 360f * (1 - progress)

            draw = true
        } else {
            // alternativer Cooldown ohne typ
            val cooldown = activeCooldowns.find { it.item == null }
            if(cooldown != null) {
                val elapsedTime = currentTime - cooldown.startTime
                val progress = elapsedTime.toFloat() / cooldownDuration
                currentSweepAngle = 360f * (1 - progress)

                draw = true
            }
        }

        if (draw) {
            // Begrenze den Sweep-Angle auf maximal 360 Grad (vollständiger Kreis)
            if (currentSweepAngle <= 0f) {
                if (model.player.model.hasFiredPing) {
                    model.player.model.hasFiredPing = false
                }

                if (model.player.model.hasUnlimitedPings) {
                    model.player.model.hasUnlimitedPings = false
                    (item as ItemController)?.isPickedUp = false
                    (item as ItemController)?.isUsed = true
                }

                removeCooldown(item)
            }

            // Berechne die Grenzen des Kreises
            circleBounds.left = centerX - radius
            circleBounds.top = centerY - radius
            circleBounds.right = centerX + radius
            circleBounds.bottom = centerY + radius

            // Zeichne den gefüllten Kreis im Uhrzeigersinn
            canvas.drawArc(circleBounds, startAngle, currentSweepAngle, true, circlePaint)
        }

    }
}


