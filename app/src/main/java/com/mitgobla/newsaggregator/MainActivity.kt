package com.mitgobla.newsaggregator

import android.app.job.JobInfo.NETWORK_TYPE_UNMETERED
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
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
    private var refreshButtonVisible = false

    private var currentFragment: Int = 0
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private lateinit var bottomNavigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup the API worker to run periodically
        val periodicApiWorkerConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicApiWorkRequest = PeriodicWorkRequestBuilder<NewsApiWorker>(30, TimeUnit.MINUTES).setConstraints(periodicApiWorkerConstraints)
            .setInputData(Data.Builder().putBoolean("periodic", true).build())
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("backgroundApiTask", ExistingPeriodicWorkPolicy.KEEP,periodicApiWorkRequest)
        // Call the API once to get the latest news
        callApi()

        // If we are logged in, make sure we have the topics in the user database
        TopicInitializer.setupTopics(this)

        // set activity layout to main
        setContentView(R.layout.activity_main)

        // set the toolbar
        val toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)


        searchButtonVisible = savedInstanceState?.getBoolean("searchButtonVisible") ?: false
        signOutButtonVisible = savedInstanceState?.getBoolean("signOutButtonVisible") ?: false
        refreshButtonVisible = savedInstanceState?.getBoolean("refreshButtonVisible") ?: false

        // values for fragments, that are used as tabs in the bottom navigation
        val fragments = listOf(FrontPageFragment(), TopicsFragment(), ProfileFragment())

        currentFragment = savedInstanceState?.getInt("currentFragment") ?: 0
        setCurrentFragment(fragments[currentFragment])

        bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                Log.d("MainActivity", "onAuthStateChanged:signed_out")
            } else {
                Log.d("MainActivity", "onAuthStateChanged:signed_in:" + user.uid)
            }
            setupBottomNavigation()
        }
        FirebaseAuth.getInstance().addAuthStateListener(authListener)

        bottomNavigationBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeAction -> {
                    currentFragment = 0
                    setCurrentFragment(fragments[currentFragment])
                    searchButtonVisible = false
                    signOutButtonVisible = false
                    refreshButtonVisible = true
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.topicsAction -> {
                    currentFragment = 1
                    setCurrentFragment(fragments[currentFragment])
                    searchButtonVisible = true
                    signOutButtonVisible = false
                    refreshButtonVisible = false
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profileAction -> {
                    currentFragment = 2
                    setCurrentFragment(fragments[currentFragment])
                    // If user is signed in, show sign out button
                    val user = GoogleSignIn.getLastSignedInAccount(this)
                    searchButtonVisible = false
                    signOutButtonVisible = user != null
                    refreshButtonVisible = false
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    currentFragment = 0
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
        setupBottomNavigation()
    }

    private fun callApi() {
        val apiWorker = OneTimeWorkRequestBuilder<NewsApiWorker>()
            .setInputData(Data.Builder().putBoolean("periodic", false).build())
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork("updateFrontPageFromApi", ExistingWorkPolicy.REPLACE, apiWorker)
    }

    private fun setupBottomNavigation() {
        val user = GoogleSignIn.getLastSignedInAccount(this)
        bottomNavigationBar.menu.findItem(R.id.topicsAction).isVisible = user != null
        Log.d("MainActivity", "setupBottomNavigation: ${user != null}")
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
        val refreshItem = menu?.findItem(R.id.toolbarRefresh)
        if (refreshItem != null) {
            refreshItem.isVisible = refreshButtonVisible
        }
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate((R.menu.toolbar_front_page), menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbarRefresh -> {
                callApi()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean("searchButtonVisible", searchButtonVisible)
        outState.putBoolean("signOutButtonVisible", signOutButtonVisible)
        outState.putBoolean("refreshButtonVisible", refreshButtonVisible)
        outState.putInt("currentFragment", currentFragment)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    }

}