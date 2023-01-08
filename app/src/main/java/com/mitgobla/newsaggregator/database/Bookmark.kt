package com.mitgobla.newsaggregator.database

/**
 * Stores information about a bookmark.
 * A bookmark is an article that the user has saved for later - saving it this way
 * means that if a user signs in with a different device, the bookmark will be able to be
 * retrieved.
 */
data class Bookmark(
    val title: String = "",
    var url: String = "",
    var imageUrl: String = "",
    var content: String = "",
    var authorName: String = "",
    var authorUrl: String = "",
    var topic: String = ""
)
