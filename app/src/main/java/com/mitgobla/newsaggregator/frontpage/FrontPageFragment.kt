package com.mitgobla.newsaggregator.frontpage

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.service.NewsApiWorker
import com.mitgobla.newsaggregator.topics.Topic
import com.mitgobla.newsaggregator.topics.TopicInitializer
import kotlin.properties.Delegates

class FrontPageFragment:Fragment(R.layout.fragment_front_page) {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private lateinit var topics: ArrayList<Topic>

    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private var currentTab = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.frontPageViewPager)
        tabLayout = view.findViewById(R.id.frontPageTabs)

        // listen for authentication changes and update the topics on change
        authListener = FirebaseAuth.AuthStateListener {
            checkForTopics()
        }
        FirebaseAuth.getInstance().addAuthStateListener { authListener }
    }

    private fun checkForTopics() {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        topics = arrayListOf()
        if (account != null) {
            Log.i("FrontPageFragment", "Logged in, getting topics from database")
            // ensure topics are initialized
            TopicInitializer.setupTopics(requireContext())
            val db = Firebase.firestore
            val topicsRef = db.collection("topics").whereEqualTo("favourite", true)
            topicsRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val topic = document.toObject(Topic::class.java)
                    Log.i("FrontPageFragment", "Adding Topic: $topic")
                    topics.add(topic)
                }
                viewPager.adapter = FrontPageAdapter(this, topics)
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = topics[position].topic
                }.attach()
                viewPager.currentItem = currentTab
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
            viewPager.currentItem = currentTab
        }

        val apiWorker = OneTimeWorkRequestBuilder<NewsApiWorker>()
            .setInputData(Data.Builder().putBoolean("periodic", false).build())
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniqueWork("updateFrontPageFromApi", ExistingWorkPolicy.REPLACE, apiWorker)
    }

    private fun resumeLastPosition() {
        Log.d("FrontPageFragment", "Resuming last position $currentTab")
        tabLayout.setScrollPosition(currentTab, 0f, true)
        viewPager.currentItem = currentTab
    }

    override fun onStart() {
        super.onStart()
        checkForTopics()
        resumeLastPosition()
    }

    override fun onResume() {
        super.onResume()
        resumeLastPosition()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentTab = viewPager.currentItem
        FirebaseAuth.getInstance().removeAuthStateListener { authListener }
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