package com.mitgobla.newsaggregator.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlin.coroutines.CoroutineContext

class ArticleViewModel(private val repository: ArticleRepository) : ViewModel() {
        fun getArticlesByTopic(topic: String) = repository.getArticlesByTopic(topic).asLiveData()
}