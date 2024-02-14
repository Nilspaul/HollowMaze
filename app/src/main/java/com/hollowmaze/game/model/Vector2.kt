package com.hollowmaze.game.model

import android.util.Log
import com.hollowmaze.game.controller.helper.Position
import kotlin.math.abs
import kotlin.math.sqrt

data class Vector2(val x: Float, val y: Float) {

    operator fun plus(vector: Vector2) = Vector2(x + vector.x, y + vector.y)
    operator fun minus(vector: Vector2) = Vector2(x - vector.x, y - vector.y)
    operator fun times(vector: Vector2) = x * vector.x + y * vector.y
    operator fun times(scalar: Float) = Vector2(x * scalar, y * scalar)
    operator fun div(scalar: Float) = Vector2(x / scalar, y / scalar)


    fun cross(vector: Vector2) = (x * vector.y)- (y*vector.x)


    fun getLength(): Float {
        return sqrt(x*x + y*y)
    }

    fun getDistance(vector: Vector2): Float {
        return (vector - this).getLength()
    }

    fun normalize(): Vector2 {
        return this / getLength()
    }

    fun getDotProduct( vector : Vector2) : Float{
        return this.x * vector.x + this.y * vector.y
    }
    fun getScalarProduct(vector: Vector2) :Float{
      return  x *vector.x + y * vector.y
    }

    operator fun unaryMinus(): Vector2 {
        return Vector2(-x,-y)
    }
}

fun Position.toVector() = Vector2(x,y)