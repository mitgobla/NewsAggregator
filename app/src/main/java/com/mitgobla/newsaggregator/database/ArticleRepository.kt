package com.mitgobla.newsaggregator.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/**
 * Repository for the article database.
 * Serves as a single source to retrieve and insert articles into the database,
 * and observe changes to the database.
 */
class ArticleRepository(private val articleDao: ArticleDao) {
    /**
     * Returns a Flow list of articles from a specific topic.
     * Can be observed for changes.
     */
    fun getArticlesByTopic(topic: String): Flow<List<Article>> {
        return articleDao.getArticlesByTopic(topic)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(article: Article) {
        articleDao.insert(article)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(vararg articles: Article) {
        articleDao.insertAll(*articles)
    }
}