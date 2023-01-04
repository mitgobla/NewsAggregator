package com.mitgobla.newsaggregator.frontpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mitgobla.newsaggregator.topics.Topic
import com.mitgobla.newsaggregator.topics.TopicRepository

class TopicTabViewModel(private val repository: TopicRepository) : ViewModel() {
    val favouriteTopics: LiveData<List<Topic>> = repository.favouriteTopics.asLiveData()
}