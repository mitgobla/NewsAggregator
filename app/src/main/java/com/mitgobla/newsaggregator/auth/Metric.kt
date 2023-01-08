package com.mitgobla.newsaggregator.auth

/**
 * Stores information about a topic metric,
 * which is the number of times the user has read an article from that topic.
 */
data class Metric(
    val topic: String = "",
    val count: Int = 0
)
