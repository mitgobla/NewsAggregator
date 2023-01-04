package com.mitgobla.newsaggregator.frontpage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.topics.TopicsApplication

class FrontPageFragment:Fragment(R.layout.fragment_front_page) {

    private val topicTabViewModel: TopicTabViewModel by viewModels {
        TopicTabViewModelFactory((activity?.application as TopicsApplication).repository)
    }

    private lateinit var frontPageTabs : TabLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        frontPageTabs = view.findViewById(R.id.frontPageTabs)

        topicTabViewModel.favouriteTopics.observe(viewLifecycleOwner) { topics ->
            frontPageTabs.removeAllTabs()
            frontPageTabs.addTab(frontPageTabs.newTab().setText(R.string.breaking))
            frontPageTabs.addTab(frontPageTabs.newTab().setText(R.string.following))
            topics.forEach { topic ->
                frontPageTabs.addTab(frontPageTabs.newTab().setText(topic.topic))
            }
        }
    }

}