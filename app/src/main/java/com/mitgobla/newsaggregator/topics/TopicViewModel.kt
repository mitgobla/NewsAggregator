package com.mitgobla.newsaggregator.topics

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TopicViewModel(private val repository: TopicRepository) : ViewModel() {

    // We use LiveData and caching (with the repository) so that the UI does not have
    // to poll the database for changes, instead the UI only updates when the data changes
    val allTopics: LiveData<List<Topic>> = repository.allTopics.asLiveData()

    var searchTopic: LiveData<List<Topic>> = repository.searchTopics("").asLiveData()

    fun updateTopic(topic: Topic) = viewModelScope.launch {
        repository.updateTopic(topic)
    }

    fun searchTopics(query: String): LiveData<List<Topic>> {
        searchTopic = repository.searchTopics(query).asLiveData()
        return searchTopic
    }


}