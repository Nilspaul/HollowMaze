package com.hollowmaze.game.controller.helper

class simpleMath {
    companion object {
        fun roundUpToNearestHundred(number: Int): Int {
            return (number + 99) / 100 * 100
        }
    }
}