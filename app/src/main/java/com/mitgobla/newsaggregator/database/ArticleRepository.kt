package com.mitgobla.newsaggregator.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ArticleRepository(private val articleDao: ArticleDao) {
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