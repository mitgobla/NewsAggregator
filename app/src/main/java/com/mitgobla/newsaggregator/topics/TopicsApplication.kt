package com.mitgobla.newsaggregator.topics

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TopicsApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { TopicRoomDatabase.getDatabase(this, scope = applicationScope)}
    val repository by lazy {TopicRepository(database.topicDao())}
}