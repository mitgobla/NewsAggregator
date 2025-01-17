package com.mitgobla.newsaggregator.frontpage

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

/**
 * Activity for displaying the news article.
 */
class NewsArticleActivity : AppCompatActivity(), CommentDialogFragment.CommentDialogListener {

    private lateinit var authenticationListener : FirebaseAuth.AuthStateListener

    private lateinit var articleUrl : String
    private lateinit var bookmarkMenuItem : MenuItem
    private lateinit var newsArticleCommentAdapter : CommentsAdapter
    private var bookmarked : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        // Get the article information from the intent
        val articleHeader = intent.getStringExtra("articleTitle")
        val articleImageUrl = intent.getStringExtra("articleImageUrl")
        articleUrl = intent.getStringExtra("articleUrl").toString()
        val articleContent = intent.getStringExtra("articleContent")
        val articleAuthorName = intent.getStringExtra("articleAuthorName")
        val articleAuthorUrl = intent.getStringExtra("articleAuthorUrl")
        val articleTopic = intent.getStringExtra("articleTopic")

        // Increment the read count for the article topic
        if (articleTopic != null) {
            incrementMetrics(articleTopic)
        }


        // Ensure back button is visible
        val toolbar = findViewById<MaterialToolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Set the article header
        val newsArticleHeader = findViewById<AppCompatTextView>(R.id.newsArticleHeader)
        newsArticleHeader.text = articleHeader
        newsArticleHeader.contentDescription = articleHeader

        // Set the article image, using the Coil library to lazy load the image
        val newsArticleImage = findViewById<AppCompatImageView>(R.id.newsArticleImage)
        newsArticleImage.load(articleImageUrl) {
            crossfade(true)
            placeholder(ColorDrawable(ContextCompat.getColor(applicationContext, R.color.loadingColor)))
            transformations(RoundedCornersTransformation(10f))
        }

        // Set the article author, which formats into a clickable link of the author website
        val newsArticleAuthor = findViewById<AppCompatTextView>(R.id.authorName)
        val authorWithUrl = "<p>${getString(R.string.articleBy)}<a href='$articleAuthorUrl'> $articleAuthorName</a></p>"
        newsArticleAuthor.text = Html.fromHtml(authorWithUrl, Html.FROM_HTML_MODE_COMPACT)
        newsArticleAuthor.movementMethod = LinkMovementMethod.getInstance()

        // Set the article content
        val newsArticleContent = findViewById<AppCompatTextView>(R.id.newsArticleBody)
        newsArticleContent.text = articleContent
        newsArticleContent.contentDescription = articleContent



        // Setup actions for the bottom navigation
        // The bookmark button icon changes depending on whether the article is bookmarked or not
        // The share button runs an implicit intent to allow the user to share the article
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bookmarkAction -> {
                    bookmarked = !bookmarked
                    if (bookmarked) {
                        bookmarkArticle(articleHeader!!,
                            articleUrl, articleImageUrl!!, articleContent!!, articleAuthorName!!, articleAuthorUrl!!, articleTopic!!)
                        setBookmarkIcon()
                    } else {
                        unbookmarkArticle(articleUrl)
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

        // Set the add comment button visibility depending on if the user is logged in
        val newsArticleAddCommentButton = findViewById<AppCompatButton>(R.id.addCommentButton)
        newsArticleAddCommentButton.setOnClickListener {
            onAddCommentClicked()
        }
        // Setup the comments
        val newsArticleCommentRecyclerView = findViewById<RecyclerView>(R.id.commentsRecyclerView)
        newsArticleCommentAdapter = CommentsAdapter()
        newsArticleCommentRecyclerView.adapter = newsArticleCommentAdapter
        newsArticleCommentRecyclerView.layoutManager = LinearLayoutManager(this)

        // Hide the bookmark item if the user is not logged in
        bookmarkMenuItem = bottomNavigationView.menu.findItem(R.id.bookmarkAction)
        authenticationListener = FirebaseAuth.AuthStateListener {
            val user = GoogleSignIn.getLastSignedInAccount(this)
            bookmarkMenuItem.isVisible = user != null
            newsArticleAddCommentButton.isVisible = user != null
        }
        FirebaseAuth.getInstance().addAuthStateListener(authenticationListener)

        checkIfBookmarked(articleUrl)
        getComments(articleUrl)
    }

    /**
     * Increment the read count for the article topic.
     */
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

    /**
     * Check if the article is bookmarked already, and update
     * the bookmark icon accordingly.
     */
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

    /**
     * Get the comments for the article.
     */
    private fun getComments(url: String) {
        val db = Firebase.firestore
        db.collection("comments").whereEqualTo("articleUrl", url).get().addOnSuccessListener { documents ->
            val comments = mutableListOf<Comment>()
            for (document in documents) {
                val comment = document.toObject(Comment::class.java)
                comments.add(comment)
            }
            newsArticleCommentAdapter.submitList(comments)
        }
    }

    /**
     * Display the add comment dialog when the add comment button is clicked.
     */
    private fun onAddCommentClicked() {
        val commentDialogFragment = CommentDialogFragment()
        commentDialogFragment.show(supportFragmentManager, "commentDialog")
    }

    /**
     * Set the bookmark icon depending on whether the article is bookmarked or not.
     */
    private fun setBookmarkIcon() {
        if (bookmarked) {
            bookmarkMenuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_bookmark_added_24)
            bookmarkMenuItem.isChecked = true
        } else {
            bookmarkMenuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_bookmark_border_24)
            bookmarkMenuItem.isChecked = false
        }
    }

    /**
     * Add the article to the user's bookmarks.
     */
    private fun bookmarkArticle(title: String, url: String, imageUrl: String, content: String, authorName: String, authorUrl: String, articleTopic: String) {
        val user = GoogleSignIn.getLastSignedInAccount(this)
        if (user != null) {
            val db = Firebase.firestore
            val bookmark = Bookmark(title, url, imageUrl, content, authorName, authorUrl, articleTopic)
            // check if bookmark already exists in database, if not add it
            db.collection("bookmarks").document(url.hashCode().toString()).get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    db.collection("bookmarks").document(url.hashCode().toString()).set(bookmark)
                }
            }
        }
    }

    /**
     * Remove the article from the user's bookmarks.
     */
    private fun unbookmarkArticle(url: String) {
        val user = GoogleSignIn.getLastSignedInAccount(this)
        if (user != null) {
            val db = Firebase.firestore
            db.collection("bookmarks").document(url.hashCode().toString()).delete()
        }
    }

    /**
     * Set the back button to close the activity.
     */
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

    /**
     * Add the comment to the database when the dialog is submitted.
     */
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        val commentEditText = dialog.dialog?.findViewById<AppCompatEditText>(R.id.commentInputTextBox)
        val comment = commentEditText?.text.toString()
        if (comment.isNotEmpty()) {
            val user = GoogleSignIn.getLastSignedInAccount(this)
            if (user != null) {
                val db = Firebase.firestore
                val commentObject = Comment(user.displayName!!, comment, articleUrl)
                db.collection("comments").add(commentObject)
                getComments(articleUrl)
            }
        }
    }
}