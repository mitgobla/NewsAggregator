package com.mitgobla.newsaggregator.database

data class Bookmark(
    val title: String = "",
    var url: String = "",
    var imageUrl: String = "",
    var content: String = "",
    var authorName: String = "",
    var authorUrl: String = ""
)
