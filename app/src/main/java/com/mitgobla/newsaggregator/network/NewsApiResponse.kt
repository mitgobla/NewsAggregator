package com.mitgobla.newsaggregator.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewsApiResponse(
    @Json(name = "totalArticles") val totalArticles: Int,
    @Json(name = "articles") val articles: List<ArticleResponse>
)
