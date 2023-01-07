package com.mitgobla.newsaggregator

import android.app.job.JobInfo.NETWORK_TYPE_UNMETERED
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mitgobla.newsaggregator.auth.ProfileFragment
import com.mitgobla.newsaggregator.frontpage.FrontPageFragment
import com.mitgobla.newsaggregator.service.NewsApiWorker
import com.mitgobla.newsaggregator.topics.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var searchButtonVisible = false
    private var signOutButtonVisible = false

    private var currentFragment: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup the API worker to run periodically
        val periodicApiWorkerConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicApiWorkRequest = PeriodicWorkRequestBuilder<NewsApiWorker>(30, TimeUnit.MINUTES).setConstraints(periodicApiWorkerConstraints).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("backgroundApiTask", ExistingPeriodicWorkPolicy.KEEP,periodicApiWorkRequest)

        // If we are logged in, make sure we have the topics in the user database
        TopicInitializer.setupTopics(this)

        // set activity layout to main
        setContentView(R.layout.activity_main)

        // set the toolbar
        val toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        searchButtonVisible = false

        // values for fragments, that are used as tabs in the bottom navigation
        val fragments = listOf(FrontPageFragment(), TopicsFragment(), MapFragment(), ProfileFragment())

        currentFragment = savedInstanceState?.getInt("currentFragment") ?: 0
        setCurrentFragment(fragments[currentFragment])

        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeAction -> {
                    setCurrentFragment(fragments[0])
                    currentFragment = 0
                    searchButtonVisible = false
                    signOutButtonVisible = false
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.topicsAction -> {
                    setCurrentFragment(fragments[1])
                    currentFragment = 1
                    searchButtonVisible = true
                    signOutButtonVisible = false
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.mapAction -> {
                    setCurrentFragment(fragments[2])
                    currentFragment = 2
                    searchButtonVisible = false
                    signOutButtonVisible = false
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profileAction -> {
                    setCurrentFragment(fragments[3])
                    currentFragment = 3
                    searchButtonVisible = false
                    // if user is signed in, show sign out button
                    val firebaseAuth = FirebaseAuth.getInstance()
                    if (firebaseAuth.currentUser != null) {
                        signOutButtonVisible = true
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    currentFragment = 0
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.fragmentMain, fragment)
        commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        val searchItem = menu?.findItem(R.id.toolbarSearch)
        if (searchItem != null) {
            searchItem.isVisible = searchButtonVisible
        }
        val signOutItem = menu?.findItem(R.id.toolbarSignOut)
        if (signOutItem != null) {
            signOutItem.isVisible = signOutButtonVisible
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate((R.menu.toolbar_front_page), menu)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean("searchButtonVisible", searchButtonVisible)
        outState.putBoolean("signOutButtonVisible", signOutButtonVisible)
    }

}