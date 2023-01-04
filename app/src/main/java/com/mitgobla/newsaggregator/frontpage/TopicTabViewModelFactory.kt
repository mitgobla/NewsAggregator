package com.mitgobla.newsaggregator.frontpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mitgobla.newsaggregator.topics.TopicRepository
import com.mitgobla.newsaggregator.topics.TopicViewModel

class TopicTabViewModelFactory(private val repository: TopicRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TopicTabViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TopicTabViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}