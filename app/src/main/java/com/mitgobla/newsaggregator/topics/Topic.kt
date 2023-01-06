package com.mitgobla.newsaggregator.topics

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Fts4(notIndexed = ["notify", "favourite"])
@Entity(tableName = "topics_table")
data class Topic(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Int,
    @ColumnInfo(name = "topic") val topic: String,
    @ColumnInfo(name = "favourite") var favourite: Boolean,
    @ColumnInfo(name = "notify") var notify: Boolean,
    @ColumnInfo(name = "readCount") var readCount: Int,
    @ColumnInfo(name = "required") val required: Boolean
)
