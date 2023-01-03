package com.mitgobla.newsaggregator.topics

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R

class TopicsFragment:Fragment(R.layout.fragment_topics) {

    // create topics factory
    private val topicViewModel: TopicViewModel by viewModels {
        TopicViewModelFactory((activity?.application as TopicsApplication).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topicRecyclerView = view.findViewById<RecyclerView>(R.id.topicsRecyclerView)

        val topicAdapter = TopicListAdapter {
            topicViewModel.updateTopic(it)
        }
        topicRecyclerView.adapter = topicAdapter
        topicRecyclerView.layoutManager = LinearLayoutManager(view.context)

        // create observer for the topics LiveData
        topicViewModel.allTopics.observe(viewLifecycleOwner) { topics ->
            topics.let { topicAdapter.submitList(it) }
        }


    }
}