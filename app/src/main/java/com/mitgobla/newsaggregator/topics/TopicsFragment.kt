package com.mitgobla.newsaggregator.topics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R

class TopicsFragment:Fragment(R.layout.fragment_topics) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topicRecyclerView = view.findViewById<RecyclerView>(R.id.topicsRecyclerView)
        topicRecyclerView.layoutManager = LinearLayoutManager(view.context)
        val data = ArrayList<TopicViewModel>()

        var favourite = true
        for (i in 1..50) {
            favourite = !favourite
            data.add(TopicViewModel("Item $i", favourite, favourite))
        }

        val adapter = TopicAdapter(data)
        topicRecyclerView.adapter = adapter
    }
}