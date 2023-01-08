package com.mitgobla.newsaggregator.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for the News API.
 * Uses Retrofit to create a service for the API.
 * Uses Moshi to convert the JSON response to Kotlin objects.
 */
interface NewsApiInterface {

    /**
     * Get the trending headlines from the News API.
     */
    @GET("top-headlines?lang=en")
    fun getTopHeadlines(
        @Query("token") token: String
    ): Call<NewsApiResponse>

    /**
     * Get articles from the News API based on a search query.
     */
    @GET("search?lang=en")
    fun searchByQuery(
        @Query("q") query: String,
        @Query("token") token: String
    ): Call<NewsApiResponse>

    // Ensures that only one instance of the service is created.
    companion object {
        private var BASE_URL = "https://gnews.io/api/v4/"

        fun create(): NewsApiInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(NewsApiInterface::class.java)
        }
    }
}