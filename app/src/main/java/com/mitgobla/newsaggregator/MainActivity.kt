package com.mitgobla.newsaggregator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mitgobla.newsaggregator.frontpage.FrontPageFragment
import com.mitgobla.newsaggregator.topics.TopicViewModel
import com.mitgobla.newsaggregator.topics.TopicViewModelFactory
import com.mitgobla.newsaggregator.topics.TopicsApplication
import com.mitgobla.newsaggregator.topics.TopicsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set activity layout to main
        setContentView(R.layout.activity_main)

        // set the toolbar
        val toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)

        // values for fragments
        val frontPageFragment = FrontPageFragment()
        val mapFragment = MapFragment()
        val topicsFragment = TopicsFragment()

        setCurrentFragment(frontPageFragment)

        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeAction -> {
                    setCurrentFragment(frontPageFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.topicsAction -> {
                    setCurrentFragment(topicsFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.mapAction -> {
                    setCurrentFragment(mapFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profileAction -> {
                    setCurrentFragment(frontPageFragment)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate((R.menu.toolbar_front_page), menu)
        return super.onCreateOptionsMenu(menu)
    }

}