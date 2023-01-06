package com.mitgobla.newsaggregator.topics

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Ignore
import androidx.room.PrimaryKey

data class Topic(
    val topic: String? = null,
    var favourite: Boolean = false,
    var notify: Boolean = false,
    var readCount: Int = 0
)
