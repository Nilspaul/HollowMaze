package com.hollowmaze.game.controller.helper

import kotlin.math.*

/**
 * Size
 */
class Size(var width: Float, var height: Float) {
    /**
     * Liefert die mittlere Breite
     */
    val centerX: Float
        get() = width / 2

    /**
     * Liefert die mittlere Höhe
     */
    val centerY: Float
        get() = height / 2

    /**
     * Liefert die Flächengröße
     */
    val area: Float
        get() = width * height

    /**
     * Liefert den Umfang
     */
    val perimeter: Float
        get() = width*2 + height*2

    /**
     * Liefert das Seitenverhältnis
     */
    val ratio: Float
        get() = width / height

    /**
     * Liefert die maximale Größe
     */
    val maxDimension: Float
        get() = max(width, height)

    /**
     * Liefert die minimale Größe
     */
    val minDimension: Float
        get() = min(width, height)

    /**
     * Skalieren um einen Faktor
     */
    fun scale(factor: Float): Size {
        return Size(width * factor, height * factor)
    }

    /**
     * Skaliert auf eine neue Size
     */
    fun resize(newSize: Size) {
        width = newSize.width
        height = newSize.height
    }

    override fun toString(): String {
        return "Width: ${width}; Height: ${height}"
    }
}