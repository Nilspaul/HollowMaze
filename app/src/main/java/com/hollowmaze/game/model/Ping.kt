package com.hollowmaze.game.model

data class Ping(
    var startPos: Vector2,
    var endPos: Vector2,
    var startTime: Long
) {
    val speed = 2_000L // Speed of the Ping
    val multiplier = 2000 // Length
    val maxLength = 500f
    var hasCollided = false

    fun getEndPosition(): Vector2 {
        if (hasCollided) return endPos
        val elapsedTime = (System.currentTimeMillis() - startTime).coerceAtMost(speed)
        val relativeTime = elapsedTime / speed.toFloat()
        val direction = (endPos - startPos).normalize()
        return Vector2(
            startPos.x + direction.x * relativeTime * multiplier,
            startPos.y + direction.y * relativeTime * multiplier
        )
    }

    fun getStartPosition(): Vector2 {
        if (hasCollided) return endPos
        if (getEndPosition().getDistance(startPos) < maxLength) return startPos

        return getEndPosition() - (getEndPosition() - startPos).normalize() * maxLength


    }

    fun reflect(normalVector: Vector2, colPoint: Vector2): Vector2 {

        val direction = endPos - startPos
        //https://math.stackexchange.com/a/13263
        return direction - normalVector * (2f * (direction * normalVector))

    }
}