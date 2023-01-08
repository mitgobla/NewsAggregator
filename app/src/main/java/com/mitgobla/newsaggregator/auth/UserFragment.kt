package com.mitgobla.newsaggregator.auth

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.database.Bookmark
import com.mitgobla.newsaggregator.frontpage.NewsArticleActivity

class UserFragment(private var signOutClickListener: () -> Unit) : Fragment(R.layout.fragment_user) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Get the logged in user
        val user = FirebaseAuth.getInstance().currentUser

        // Display the user's profile picture
        val profilePicture = view.findViewById<AppCompatImageView>(R.id.profileImage)
        profilePicture.load(user?.photoUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_baseline_account_circle_128)
            transformations(CircleCropTransformation())
        }

        // Display the user's name
        val profileName = view.findViewById<AppCompatTextView>(R.id.profileName)
        if (user != null) {
            profileName.text = getString(R.string.profileNamePlaceholder, user.displayName)
        } else {
            profileName.text = getString(R.string.profileNamePlaceholder, getString(R.string.anonymous))
        }

        // Get the users metrics from the database
        val db = Firebase.firestore
        val metricsRef = db.collection("topics").whereGreaterThan("readCount", 0)
        val metrics = mutableListOf<Metric>()
        metricsRef.get().addOnSuccessListener { result ->
            for (document in result) {
                if (document != null) {
                    val topic = document.id
                    val count = document.getLong("readCount")?.toInt() ?: 0
                    metrics.add(Metric(topic, count))
                }
            }
            // Display the user's metrics
            val metricsRecyclerView = view.findViewById<RecyclerView>(R.id.metricsRecyclerView)
            val metricsAdapter = MetricListAdapter(metrics.maxOf { it.count })
            metricsRecyclerView.adapter = metricsAdapter
            metricsRecyclerView.layoutManager = LinearLayoutManager(context)
            // Metrics are reversed, so that the most read topic is at the top
            metrics.reverse()
            metricsAdapter.submitList(metrics)

            if (metrics.isEmpty()) {
                view.findViewById<AppCompatTextView>(R.id.profileMetricsTitle).visibility = View.GONE
            } else {
                view.findViewById<AppCompatTextView>(R.id.profileMetricsTitle).visibility = View.VISIBLE
            }
        }

        // Get the users bookmarks from the database
        val bookmarksRef = db.collection("bookmarks")
        val bookmarks = mutableListOf<Bookmark>()
        bookmarksRef.get().addOnSuccessListener {result ->
            for (document in result) {
                if (document != null) {
                    val title = document.getString("title") ?: ""
                    val url = document.getString("url") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val content = document.getString("content") ?: ""
                    val authorName = document.getString("authorName") ?: ""
                    val authorUrl = document.getString("authorUrl") ?: ""
                    val topic = document.getString("topic") ?: ""
                    bookmarks.add(Bookmark(title, url, imageUrl, content, authorName, authorUrl, topic))
                }
            }
            // Display the user's bookmarks
            val bookmarksRecyclerView = view.findViewById<RecyclerView>(R.id.bookmarksRecyclerView)
            val bookmarksAdapter = BookmarkListAdapter { bookmark ->
                onClickBookmark(bookmark)
            }
            bookmarksRecyclerView.adapter = bookmarksAdapter
            bookmarksRecyclerView.layoutManager = LinearLayoutManager(context)
            bookmarksAdapter.submitList(bookmarks)

            if (bookmarks.isEmpty()) {
                view.findViewById<AppCompatTextView>(R.id.profileBookmarksTitle).visibility = View.GONE
            } else {
                view.findViewById<AppCompatTextView>(R.id.profileBookmarksTitle).visibility = View.VISIBLE
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val signOutAction: MenuItem = menu.findItem(R.id.toolbarSignOut)
        signOutAction.setOnMenuItemClickListener {
            signOutClickListener.invoke()
            true
        }
    }

    /**
     * Run an explicit intent to open the bookmark in the NewsArticleActivity activity.
     * The article is populated from the stored bookmark.
     */
    private fun onClickBookmark(bookmark: Bookmark) {
        val intent = Intent(context, NewsArticleActivity::class.java)
        intent.putExtra("articleTopic", bookmark.topic)
        intent.putExtra("articleTitle", bookmark.title)
        intent.putExtra("articleImageUrl", bookmark.imageUrl)
        intent.putExtra("articleUrl", bookmark.url)
        intent.putExtra("articleContent", bookmark.content)
        intent.putExtra("articleAuthorName", bookmark.authorName)
        intent.putExtra("articleAuthorUrl", bookmark.authorUrl)
        startActivity(intent)
    }
}