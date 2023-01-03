package com.mitgobla.newsaggregator.topics

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TopicViewModel(private val repository: TopicRepository) : ViewModel() {

    // We use LiveData and caching (with the repository) so that the UI does not have
    // to poll the database for changes, instead the UI only updates when the data changes
    val allTopics: LiveData<List<Topic>> = repository.allTopics.asLiveData()

    fun updateTopic(topic: Topic) = viewModelScope.launch {
        repository.updateTopic(topic)
    }
}