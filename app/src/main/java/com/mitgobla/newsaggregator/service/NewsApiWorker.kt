package com.mitgobla.newsaggregator.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mitgobla.newsaggregator.MainActivity
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.database.Article
import com.mitgobla.newsaggregator.database.ArticleDatabase
import com.mitgobla.newsaggregator.network.ArticleResponse
import com.mitgobla.newsaggregator.network.NewsApiInterface
import com.mitgobla.newsaggregator.network.NewsApiResponse
import com.mitgobla.newsaggregator.topics.Topic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Call

/**
 * A worker that will fetch articles from the News API and store them in the database.
 * The worker can be set as a periodic worker to fetch articles periodically in the background.
 */
private const val TAG = "NewsApiWorker"
class NewsApiWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val topics = arrayListOf<Topic>()
        val user = GoogleSignIn.getLastSignedInAccount(applicationContext)
        // Pull only favourite topics if the user is signed in
        // Otherwise we pull from the default topics
        if (user != null) {
            val db = Firebase.firestore
            val topicsRef = db.collection("topics").whereEqualTo("favourite", true)

            // Wait for the topics to be retrieved from the database
            val topicsSnapshot = topicsRef.get().await()
            for (document in topicsSnapshot) {
                val topic = document.toObject(Topic::class.java)
                topics.add(topic)
            }
            // Run the API call for each topic
            return pullArticles(topics, inputData.getBoolean("periodic", false))
        } else {
            val topicsRef = applicationContext.resources.getStringArray(R.array.topics_offline)
            for (topic in topicsRef) {
                topics.add(Topic(topic))
            }
            // Run the API call for each topic
            return pullArticles(topics, inputData.getBoolean("periodic", false))
        }
    }

    /**
     * Perform API call for each topic and store the articles in the local database
     */
    private suspend fun pullArticles(topics: ArrayList<Topic>, periodic: Boolean): Result {
        val database = ArticleDatabase.getDatabase(applicationContext)

        for (topic in topics) {
            // Sleep for 5 seconds to avoid rate limiting
            withContext(Dispatchers.IO) {
                Thread.sleep(5000)
            }
            val apiInterface: Call<NewsApiResponse> = if (topic.topic == applicationContext.getString(R.string.topic_breaking_news)) {
                NewsApiInterface.create().getTopHeadlines(applicationContext.getString(R.string.gnews_token))
            } else {
                NewsApiInterface.create().searchByQuery(topic.topic!!, applicationContext.getString(R.string.gnews_token))
            }

            // Call the api and wait for the response
            val response = apiInterface.execute()

            if (response.isSuccessful) {
                if (response.body() != null) {
                    val articles = response.body()!!.articles
                    for (article in articles) {
                        val alreadyExists = database.articleDao().exists(article.url)
                        // Don't add the article if it already exists in the database
                        if (!alreadyExists) {
                            database.articleDao().insert(articleResponseToArticle(topic, article))
                            // If we are a periodic worker, send a notification to the user
                            // that a new article has been added to the database from their favourite topic
                            if (periodic && topic.notify) {
                                newArticleNotification(article.title, article.description)
                            }
                        }
                    }
                }
            }
        }
        return Result.success(Data.Builder().putString("result", "success").build())
    }

    /**
     * Method for converting an ArticleResponse object to an Article object
     */
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

    /**
     * Method for creating a notification intent and sending a notification to the user
     */
    private fun newArticleNotification(title: String, description: String) {
        val notificationIntent = PendingIntent.getActivity(applicationContext, 0, Intent(applicationContext, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(applicationContext, "news_aggregator")
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(notificationIntent)
                .setSubText(applicationContext.resources.getString(R.string.appName))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, builder.build())
        }
    }

}