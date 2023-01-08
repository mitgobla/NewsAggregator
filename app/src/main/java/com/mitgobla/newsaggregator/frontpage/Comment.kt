package com.mitgobla.newsaggregator.frontpage

/**
 * Stores information about a comment.
 * A comment is a reply to an article.
 */
data class Comment(
    val by: String = "",
    val comment: String = "",
    val articleUrl: String = ""
)
