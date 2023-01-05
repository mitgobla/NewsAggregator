package com.mitgobla.newsaggregator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mitgobla.newsaggregator.auth.ProfileFragment
import com.mitgobla.newsaggregator.frontpage.FrontPageFragment
import com.mitgobla.newsaggregator.topics.TopicViewModel
import com.mitgobla.newsaggregator.topics.TopicViewModelFactory
import com.mitgobla.newsaggregator.topics.TopicsApplication
import com.mitgobla.newsaggregator.topics.TopicsFragment

class MainActivity : AppCompatActivity() {

    private var searchButtonVisible = false
    private var signOutButtonVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set activity layout to main
        setContentView(R.layout.activity_main)

        // set the toolbar
        val toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        searchButtonVisible = false

        // values for fragments
        val frontPageFragment = FrontPageFragment()
        val mapFragment = MapFragment()
        val topicsFragment = TopicsFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(frontPageFragment)

        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeAction -> {
                    setCurrentFragment(frontPageFragment)
                    searchButtonVisible = false
                    signOutButtonVisible = false
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.topicsAction -> {
                    setCurrentFragment(topicsFragment)
                    searchButtonVisible = true
                    signOutButtonVisible = false
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.mapAction -> {
                    setCurrentFragment(mapFragment)
                    searchButtonVisible = false
                    signOutButtonVisible = false
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profileAction -> {
                    setCurrentFragment(profileFragment)
                    searchButtonVisible = false
                    signOutButtonVisible = true
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
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

}