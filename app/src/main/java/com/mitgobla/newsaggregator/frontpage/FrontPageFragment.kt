package com.mitgobla.newsaggregator.frontpage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.topics.Topic
import com.mitgobla.newsaggregator.topics.TopicInitializer

/**
 * The fragment used for the main front page, where it will display
 * tabs for each favourite topic (or default if not signed in) and
 * a fragment for each tab to display the news reel for that topic.
 */
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

        // Listen for authentication changes and update the topics on change
        // For example, if the user signs in, the topics will be updated to
        // include the user's favourite topics
        authListener = FirebaseAuth.AuthStateListener {
            checkForTopics()
        }
        FirebaseAuth.getInstance().addAuthStateListener { authListener }
    }

    /**
     * Check for topics to populate the tabs with.
     * If the user is signed in, the topics will be the user's favourite topics.
     * If the user is not signed in, the topics will be the default topics.
     */
    private fun checkForTopics() {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        topics = arrayListOf()
        if (account != null) {
            // Ensure topics are initialized in the user's database
            TopicInitializer.setupTopics(requireContext())
            val db = Firebase.firestore
            // Get all favourite topics for the user
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
                viewPager.currentItem = currentTab
            }
        } else {
            // if we are not logged in, get the default topics.
            // Using an array from resources - prevents use of hardcoded strings.
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
    }

    /**
     * Set the tab to the last position when the user returns.
     */
    private fun resumeLastPosition() {
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

    override fun onPause() {
        super.onPause()
        currentTab = viewPager.currentItem
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