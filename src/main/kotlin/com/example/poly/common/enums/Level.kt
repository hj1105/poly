package com.example.poly.common.enums

enum class Level {
    LOW, MIDDLE, HIGH;

    companion object {
        fun fromProblemLevel(level: Int): Level = when (level) {
            1 -> LOW
            in 2..4 -> MIDDLE
            5 -> HIGH
            else -> throw IllegalArgumentException("Invalid problem level: $level")
        }
    }
}