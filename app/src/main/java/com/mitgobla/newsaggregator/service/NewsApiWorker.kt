package com.mitgobla.newsaggregator.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.database.Article
import com.mitgobla.newsaggregator.database.ArticleDatabase
import com.mitgobla.newsaggregator.network.ArticleResponse
import com.mitgobla.newsaggregator.network.NewsApiInterface
import com.mitgobla.newsaggregator.network.NewsApiResponse
import com.mitgobla.newsaggregator.topics.Topic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call

private const val TAG = "NewsApiWorker"
class NewsApiWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = ArticleDatabase.getDatabase(applicationContext)

        val topics = mutableListOf<Topic>()
        val user = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (user != null) {
            val db = Firebase.firestore
            val topicsRef = db.collection("topics").whereEqualTo("favourite", true)
            topicsRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val topic = document.toObject(Topic::class.java)
                    topics.add(topic)
                }
            }
            Log.i(TAG, "doWork: pulled topics from user")
        } else {
            val topicsRef = applicationContext.resources.getStringArray(R.array.topics_offline)
            for (topic in topicsRef) {
                topics.add(Topic(topic))
            }
            Log.i(TAG, "doWork: pulled topics from default")
        }

        for (topic in topics) {
            // sleep for 1 second to avoid rate limiting
            withContext(Dispatchers.IO) {
                Thread.sleep(2000)
            }
            val apiInterface: Call<NewsApiResponse> = if (topic.topic == applicationContext.getString(R.string.breaking)) {
                NewsApiInterface.create().getTopHeadlines(applicationContext.getString(R.string.gnews_token))
            } else {
                NewsApiInterface.create().searchByQuery(topic.topic!!, applicationContext.getString(R.string.gnews_token))
            }

            val response = apiInterface.execute()

            if (response.isSuccessful) {
                if (response.body() != null) {
                    val articles = response.body()!!.articles
                    for (article in articles) {
                        database.articleDao().insert(articleResponseToArticle(topic, article))
                    }
                    Log.i(TAG, "doWork: inserted articles for topic ${topic.topic}")
                }
            } else {
                Log.e(TAG, "doWork: ${response.errorBody().toString()}")
            }
        }
        Log.i(TAG, "pulled news articles")
        return Result.success(Data.Builder().putString("result", "success").build())
    }

    private fun articleResponseToArticle(topic: Topic, article: ArticleResponse): Article {
        return Article(
                topic = topic.topic!!,
                title = article.title,
                description = article.description,
                url = article.url,
                imageUrl = article.imageUrl,
                publishedAt = article.publishedAt,
                content = article.content,
                sourceName = article.source.name,
                sourceUrl = article.source.url
            )

    }

}