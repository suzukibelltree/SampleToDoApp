package com.example.sampletodoapp.util

import com.example.sampletodoapp.room.Task

fun List<Task>.groupByDeadline(): Map<String, List<Task>> {
    return this.groupBy { it.deadline } // deadlineごとにMapを作る
        .toSortedMap()
}
