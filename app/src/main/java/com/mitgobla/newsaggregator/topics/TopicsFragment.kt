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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mitgobla.newsaggregator.R

class TopicsFragment:Fragment(R.layout.fragment_topics) {

    private val searchQuery = MutableLiveData<String?>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val topicRecyclerView = view.findViewById<RecyclerView>(R.id.topicsRecyclerView)

        val topicAdapter = TopicListAdapter{ topic, favourite, notify ->
            updateTopic(topic, favourite, notify)
        }

        val db = Firebase.firestore
        val topicsRef = db.collection("topics").orderBy("favourite", Query.Direction.DESCENDING).orderBy("notify", Query.Direction.DESCENDING).orderBy("topic")
        topicsRef.get()
            .addOnSuccessListener { result ->
                val topics = mutableListOf<Topic>()
                for (document in result) {
                    val topic = document.toObject(Topic::class.java)
                    topics.add(topic)
                }
                topicAdapter.submitList(topics)
            }
            .addOnFailureListener { exception ->
                Log.w("TopicsFragment", "Error getting documents.", exception)
            }

        topicRecyclerView.adapter = topicAdapter
        topicRecyclerView.layoutManager = LinearLayoutManager(view.context)

        searchQuery.observe(viewLifecycleOwner, Observer { query ->
            topicAdapter.filter(query)
        })


    }

    private fun updateTopic(topic: String?, favourite: Boolean, notify: Boolean) {
        // Update the topic in the firebase database
        if (topic != null) {
            val db = Firebase.firestore
            val topicRef = db.collection("topics").document(topic)
            topicRef.update("favourite", favourite)
            topicRef.update("notify", notify)
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
            searchQuery.value = null
            true
        }
    }
}