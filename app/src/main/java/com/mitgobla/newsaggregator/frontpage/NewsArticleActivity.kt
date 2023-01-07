package com.mitgobla.newsaggregator.frontpage

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.database.Bookmark
import com.mitgobla.newsaggregator.topics.Topic

class NewsArticleActivity : AppCompatActivity() {

    private lateinit var authenticationListener : FirebaseAuth.AuthStateListener

    private lateinit var bookmarkMenuItem : MenuItem
    private var bookmarked : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_article)
        val articleHeader = intent.getStringExtra("articleTitle")
        val articleImageUrl = intent.getStringExtra("articleImageUrl")
        val articleUrl = intent.getStringExtra("articleUrl")
        val articleContent = intent.getStringExtra("articleContent")
        val articleAuthorName = intent.getStringExtra("articleAuthorName")
        val articleAuthorUrl = intent.getStringExtra("articleAuthorUrl")
        val articleTopic = intent.getStringExtra("articleTopic")

        if (articleTopic != null) {
            incrementMetrics(articleTopic)
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val newsArticleHeader = findViewById<AppCompatTextView>(R.id.newsArticleHeader)
        newsArticleHeader.text = articleHeader
        newsArticleHeader.contentDescription = articleHeader

        val newsArticleImage = findViewById<AppCompatImageView>(R.id.newsArticleImage)
        newsArticleImage.load(articleImageUrl) {
            crossfade(true)
            placeholder(ColorDrawable(ContextCompat.getColor(applicationContext, R.color.loadingColor)))
            transformations(RoundedCornersTransformation(10f))
        }

        val newsArticleAuthor = findViewById<AppCompatTextView>(R.id.authorName)
        val authorWithUrl = "<a href='$articleAuthorUrl'>$articleAuthorName</a>"
        newsArticleAuthor.text = Html.fromHtml(authorWithUrl, Html.FROM_HTML_MODE_COMPACT)
        newsArticleAuthor.movementMethod = LinkMovementMethod.getInstance()

        val newsArticleContent = findViewById<AppCompatTextView>(R.id.newsArticleBody)
        newsArticleContent.text = articleContent
        newsArticleContent.contentDescription = articleContent



        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bookmarkAction -> {
                    bookmarked = !bookmarked
                    if (bookmarked) {
                        bookmarkArticle(articleHeader!!, articleUrl!!, articleImageUrl!!, articleContent!!, articleAuthorName!!, articleAuthorUrl!!)
                        setBookmarkIcon()
                    } else {
                        unbookmarkArticle(articleUrl!!)
                        setBookmarkIcon()
                    }
                    true
                }
                R.id.shareAction -> {
                    // Share the article using external intent
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, articleUrl)
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.shareDescription)))
                    true
                }
                else -> false
            }
        }

        bookmarkMenuItem = bottomNavigationView.menu.findItem(R.id.bookmarkAction)
        authenticationListener = FirebaseAuth.AuthStateListener {
            val user = GoogleSignIn.getLastSignedInAccount(this)
            bookmarkMenuItem.isVisible = user != null
        }
        FirebaseAuth.getInstance().addAuthStateListener(authenticationListener)

        if (articleUrl != null) {
            checkIfBookmarked(articleUrl)
        }
    }

    private fun incrementMetrics(topic: String) {
        val user = GoogleSignIn.getLastSignedInAccount(this)
        if (user != null) {
            val db = Firebase.firestore
            val docRef = db.collection("topics").document(topic)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val topicObject = document.toObject(Topic::class.java)
                    if (topicObject != null) {
                        topicObject.readCount += 1
                        docRef.set(topicObject)
                    }
                }
            }
        }
    }

    private fun checkIfBookmarked(url: String) {
        val user = GoogleSignIn.getLastSignedInAccount(this)
        if (user != null) {
            val db = Firebase.firestore
            db.collection("bookmarks").document(url.hashCode().toString()).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val bookmark = document.toObject(Bookmark::class.java)
                    bookmarked = bookmark != null
                    setBookmarkIcon()
                }
            }
        }
    }

    private fun setBookmarkIcon() {
        if (bookmarked) {
            bookmarkMenuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_bookmark_added_24)
            bookmarkMenuItem.isChecked = true
        } else {
            bookmarkMenuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_bookmark_border_24)
            bookmarkMenuItem.isChecked = false
        }
    }

    private fun bookmarkArticle(title: String, url: String, imageUrl: String, content: String, authorName: String, authorUrl: String) {
        val user = GoogleSignIn.getLastSignedInAccount(this)
        if (user != null) {
            val db = Firebase.firestore
            val bookmark = Bookmark(title, url, imageUrl, content, authorName, authorUrl)
            // check if bookmark already exists in database, if not add it
            db.collection("bookmarks").document(url.hashCode().toString()).get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    db.collection("bookmarks").document(url.hashCode().toString()).set(bookmark)
                }
            }
        }
    }

    private fun unbookmarkArticle(url: String) {
        val user = GoogleSignIn.getLastSignedInAccount(this)
        if (user != null) {
            val db = Firebase.firestore
            db.collection("bookmarks").document(url.hashCode().toString()).delete()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseAuth.getInstance().removeAuthStateListener(authenticationListener)
    }
}