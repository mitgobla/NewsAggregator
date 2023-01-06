package com.mitgobla.newsaggregator.frontpage

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.topics.TopicsApplication

class FrontPageFragment:Fragment(R.layout.fragment_front_page) {

    private val topicTabViewModel: TopicTabViewModel by viewModels {
        TopicTabViewModelFactory((activity?.application as TopicsApplication).repository)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<ViewPager2>(R.id.frontPageViewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.frontPageTabs)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return topicTabViewModel.favouriteTopics.value?.size ?: 0
            }

            override fun createFragment(position: Int): Fragment {
                return NewsReelFragment(topicTabViewModel.favouriteTopics.value?.get(position)!!)
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = topicTabViewModel.favouriteTopics.value?.get(position)?.topic
            tab.contentDescription = topicTabViewModel.favouriteTopics.value?.get(position)?.topic
        }.attach()

        topicTabViewModel.favouriteTopics.observe(viewLifecycleOwner) { _ ->
            viewPager.adapter?.notifyDataSetChanged()
        }
    }



}