package com.mitgobla.newsaggregator.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity that describes an article.
 * Articles are stored in a local database for caching.
 */
@Entity(tableName = "articles_table")
data class Article (
    @PrimaryKey @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "topic") val topic: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "imageUrl") val imageUrl: String,
    @ColumnInfo(name = "publishedAt") val publishedAt: String,
    @ColumnInfo(name = "sourceName") val sourceName: String,
    @ColumnInfo(name = "sourceUrl") val sourceUrl: String
)