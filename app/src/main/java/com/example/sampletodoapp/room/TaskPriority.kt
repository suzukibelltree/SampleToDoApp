package com.example.sampletodoapp.room

enum class TaskPriority(val level: Int, val label: String) {
    HIGH(1, "低"),
    MEDIUM(2, "中"),
    LOW(3, "高");

    companion object {
        fun fromLevel(level: Int): TaskPriority {
            return entries.first { it.level == level }
        }

        fun fromLabel(label: String): TaskPriority {
            return entries.first { it.label == label }
        }
    }
}
