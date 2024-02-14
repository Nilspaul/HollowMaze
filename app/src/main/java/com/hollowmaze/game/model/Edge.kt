package com.hollowmaze.game.model

class Edge(val start: Vector2, val end: Vector2) {
    var direction: Vector2 = end - start
    fun getNormal( center : Vector2): Vector2 {
        val centerOfEdge = start + direction/2f
        return (centerOfEdge-center).normalize()
    }
    fun isEdgeCollidingWithEdge(
        colEdge: Edge,
    ): CollisionResult? {

        val colEdgeDirection = colEdge.end - colEdge.start
        if(colEdgeDirection.x.isNaN() || colEdgeDirection.y.isNaN()) return null
        val edgeDirection = end - start

        val den = colEdgeDirection.x * edgeDirection.y - colEdgeDirection.y * edgeDirection.x

        // Falls Denominator 0 ist, sind beide geraden parallel
        if (den == 0f) {
            return null
        }

        val pingStartToEdgeStartDirection = start - colEdge.start
        val t = (pingStartToEdgeStartDirection.x * edgeDirection.y - pingStartToEdgeStartDirection.y * edgeDirection.x) / den

        val u = -(colEdgeDirection.x * pingStartToEdgeStartDirection.y - colEdgeDirection.y * pingStartToEdgeStartDirection.x) / den

        if (t < 0f || t > 1f || u < 0f || u > 1f) {
            return null
        }

        return CollisionResult(
            this,
            colEdge.start + (colEdgeDirection * t)
        )
    }
}