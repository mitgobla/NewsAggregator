package com.mitgobla.newsaggregator.frontpage

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.service.NewsApiWorker
import com.mitgobla.newsaggregator.topics.Topic
import com.mitgobla.newsaggregator.topics.TopicInitializer

class FrontPageFragment:Fragment(R.layout.fragment_front_page) {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var topics: ArrayList<Topic> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById<ViewPager2>(R.id.frontPageViewPager)
        tabLayout = view.findViewById<TabLayout>(R.id.frontPageTabs)


        val tabSelectedPosition = savedInstanceState?.getInt("currentTab") ?: 0

        // if we are logged in, get the topics from the user database
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            Log.i("FrontPageFragment", "Logged in, getting topics from database")
            // ensure topics are initialized
            TopicInitializer.setupTopics(requireContext())

            val db = Firebase.firestore
            val topicsRef = db.collection("topics").whereEqualTo("favourite", true)

            topicsRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val topic = document.toObject(Topic::class.java)
                    topics.add(topic)
                }
                viewPager.adapter = FrontPageAdapter(this, topics)
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = topics[position].topic
                }.attach()
                if (tabSelectedPosition < topics.size) {
                    viewPager.setCurrentItem(tabSelectedPosition, false)
                } else {
                    viewPager.setCurrentItem(0, false)
                }
            }
        } else {
            // if we are not logged in, get the topics from the default database
            Log.i("FrontPageFragment", "Not logged in, getting topics from default database")
            val topicsRef = resources.getStringArray(R.array.topics_offline)
            for (topic in topicsRef) {
                topics.add(Topic(topic))
            }
            viewPager.adapter = FrontPageAdapter(this, topics)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = topics[position].topic
            }.attach()
        }

        val apiWorker = OneTimeWorkRequestBuilder<NewsApiWorker>().build()
        WorkManager.getInstance(requireContext()).enqueueUniqueWork("pullapiWorker", ExistingWorkPolicy.REPLACE, apiWorker)
    }

    override fun onStart() {
        super.onStart()


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentTab", tabLayout.selectedTabPosition)
    }

    private class FrontPageAdapter(fragment: Fragment, private val topics: List<Topic>) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return topics.size
        }

        override fun createFragment(position: Int): Fragment {
            return NewsReelFragment(topics[position])
        }
    }
}