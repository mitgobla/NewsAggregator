package com.mitgobla.newsaggregator.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArticleResponse(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "content") val content: String,
    @Json(name = "url") val url: String,
    @Json(name = "image") val imageUrl: String,
    @Json(name = "publishedAt") val publishedAt: String,
    @Json(name = "source") val source: SourceResponse
)
