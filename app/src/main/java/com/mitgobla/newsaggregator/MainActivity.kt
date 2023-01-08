package com.mitgobla.newsaggregator

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.work.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.mitgobla.newsaggregator.auth.ProfileFragment
import com.mitgobla.newsaggregator.frontpage.FrontPageFragment
import com.mitgobla.newsaggregator.service.NewsApiWorker
import com.mitgobla.newsaggregator.topics.TopicInitializer
import com.mitgobla.newsaggregator.topics.TopicsFragment
import java.util.concurrent.TimeUnit

/**
 * Newsify main activity
 */
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

        // Restore the state of the toolbar buttons
        searchButtonVisible = savedInstanceState?.getBoolean("searchButtonVisible") ?: false
        signOutButtonVisible = savedInstanceState?.getBoolean("signOutButtonVisible") ?: false
        refreshButtonVisible = savedInstanceState?.getBoolean("refreshButtonVisible") ?: false

        // values for fragments, that are used as tabs in the bottom navigation
        val fragments = listOf(FrontPageFragment(), TopicsFragment(), ProfileFragment())

        currentFragment = savedInstanceState?.getInt("currentFragment") ?: 0
        setCurrentFragment(fragments[currentFragment])

        bottomNavigationBar = findViewById(R.id.bottomNavigationView)

        authListener = FirebaseAuth.AuthStateListener {
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

    /**
     * Method for setting up a worker to call the API and update the local database
     */
    private fun callApi() {
        val apiWorker = OneTimeWorkRequestBuilder<NewsApiWorker>()
            .setInputData(Data.Builder().putBoolean("periodic", false).build())
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork("updateFrontPageFromApi", ExistingWorkPolicy.REPLACE, apiWorker)
    }

    private fun setupBottomNavigation() {
        val user = GoogleSignIn.getLastSignedInAccount(this)
        bottomNavigationBar.menu.findItem(R.id.topicsAction).isVisible = user != null
    }

    /**
     * Sets the currently displayed fragment
     */
    private fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.fragmentMain, fragment)
        commit()
    }

    /**
     * Set the visibility of the buttons in the options menu
     */
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

    /**
     * Add refresh functionality to the refresh button
     */
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