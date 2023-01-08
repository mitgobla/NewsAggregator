package com.mitgobla.newsaggregator.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents the JSON schema for an article source in the API response.
 */
@JsonClass(generateAdapter = true)
data class SourceResponse(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)
