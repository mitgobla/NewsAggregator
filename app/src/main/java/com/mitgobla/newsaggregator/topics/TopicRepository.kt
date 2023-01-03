package com.mitgobla.newsaggregator.topics

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TopicRepository(private val topicDao: TopicDao) {
    val allTopics: Flow<List<Topic>> = topicDao.getSortedTopics()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateTopic(topic: Topic) {
        topicDao.updateTopic(topic)
    }
}