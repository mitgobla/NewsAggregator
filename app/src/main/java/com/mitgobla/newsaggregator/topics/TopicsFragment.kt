package com.mitgobla.newsaggregator.topics

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R

class TopicsFragment:Fragment(R.layout.fragment_topics) {

    private val searchQuery = MutableLiveData<String?>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        val topicViewModel: TopicViewModel by viewModels {
            TopicViewModelFactory((activity?.application as TopicsApplication).repository)
        }

        super.onViewCreated(view, savedInstanceState)

        val topicRecyclerView = view.findViewById<RecyclerView>(R.id.topicsRecyclerView)

        val topicAdapter = TopicListAdapter{
            topicViewModel.updateTopic(it)
        }

        topicRecyclerView.adapter = topicAdapter
        topicRecyclerView.layoutManager = LinearLayoutManager(view.context)

        topicViewModel.allTopics.observe(viewLifecycleOwner) { topics ->
            topicAdapter.submitList(topics)
        }

        searchQuery.value = ""
        searchQuery.observe(viewLifecycleOwner) { query ->
            if (query != null) {
                if (query.isEmpty()) {
                    topicViewModel.searchTopic.removeObservers(viewLifecycleOwner)
                    topicViewModel.allTopics.observe(viewLifecycleOwner) { topics ->
                        topicAdapter.submitList(topics)
                    }
                } else {
                    topicViewModel.searchTopic.removeObservers(viewLifecycleOwner)
                    topicViewModel.allTopics.removeObservers(viewLifecycleOwner)
                    topicViewModel.searchTopics(query).observe(viewLifecycleOwner) { topics ->
                        topicAdapter.submitList(topics)
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchMenuItem = menu.findItem(R.id.toolbarSearch)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery.value = query
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery.value = newText
                return true
            }
        })

        searchView.setOnCloseListener {
            searchQuery.value = ""
            true
        }
    }
}