package com.mitgobla.newsaggregator.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiInterface {

    @GET("top-headlines?lang=en")
    fun getTopHeadlines(
        @Query("token") token: String
    ): Call<NewsApiResponse>

    @GET("search?lang=en")
    fun searchByQuery(
        @Query("q") query: String,
        @Query("token") token: String
    ): Call<NewsApiResponse>

    companion object {
        var BASE_URL = "https://gnews.io/api/v4/"

        fun create(): NewsApiInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(NewsApiInterface::class.java)
        }
    }
}