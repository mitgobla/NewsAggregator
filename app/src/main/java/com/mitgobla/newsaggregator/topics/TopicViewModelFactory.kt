package com.mitgobla.newsaggregator.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TopicViewModelFactory(private val repository: TopicRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TopicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TopicViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}