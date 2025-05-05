package com.example.sampletodoapp.room

/**
 * タスクの優先度を表す列挙型
 * @param level 優先度のレベル
 * @param label 優先度のラベル
 */
enum class TaskPriority(val level: Int, val label: String) {
    HIGH(1, "低"),
    MEDIUM(2, "中"),
    LOW(3, "高");

    companion object {
        fun fromLevel(level: Int): TaskPriority {
            return entries.first { it.level == level }
        }
    }
}
