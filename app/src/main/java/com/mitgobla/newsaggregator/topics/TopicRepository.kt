package com.mitgobla.newsaggregator.topics

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TopicRepository(private val topicDao: TopicDao) {
    val allTopics: Flow<List<Topic>> = topicDao.getSortedTopics()

    val favouriteTopics: Flow<List<Topic>> = topicDao.getFavouriteTopics()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateTopic(topic: Topic) {
        topicDao.updateTopic(topic)
    }

    fun searchTopics(query: String): Flow<List<Topic>> {
        return topicDao.searchTopics(query)
    }
}