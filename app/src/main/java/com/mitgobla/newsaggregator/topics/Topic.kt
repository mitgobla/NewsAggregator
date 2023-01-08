package com.mitgobla.newsaggregator.topics

/**
 * Stores information about a topic.
 */
data class Topic(
    val topic: String? = null,
    var favourite: Boolean = false,
    var notify: Boolean = false,
    var readCount: Int = 0
)
