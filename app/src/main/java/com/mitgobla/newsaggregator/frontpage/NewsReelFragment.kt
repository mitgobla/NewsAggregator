package com.mitgobla.newsaggregator.frontpage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.database.Article
import com.mitgobla.newsaggregator.database.ArticleApplication
import com.mitgobla.newsaggregator.database.ArticleViewModel
import com.mitgobla.newsaggregator.database.ArticleViewModelFactory
import com.mitgobla.newsaggregator.topics.Topic

/**
 * Fragment for displaying the articles for a specific topic.
 */
class NewsReelFragment(val topic: Topic) : Fragment(R.layout.fragment_news_reel) {

    private val articleViewModel: ArticleViewModel by viewModels() {
        ArticleViewModelFactory((activity?.application as ArticleApplication).repository)
    }

    private lateinit var newsReelRecyclerView: RecyclerView
    private lateinit var newsReelAdapter: NewsReelAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show a loading circle
        val loadingFrame = view.findViewById<View>(R.id.newsReelLoadingFrame)
        loadingFrame.visibility = View.VISIBLE
        // Hide the no articles text
        val statusText = view.findViewById<View>(R.id.newsReelStatusText)
        statusText.visibility = View.GONE
        // Hide the recycler view
        newsReelRecyclerView = view.findViewById(R.id.newsReelRecyclerView)
        newsReelRecyclerView.layoutManager = LinearLayoutManager(context)
        newsReelRecyclerView.visibility = View.GONE


        newsReelAdapter = NewsReelAdapter(topic) { article, topic ->
            onArticleClicked(article, topic)
        }
        // Save the recyclerview state, and restore it when the data is loaded
        newsReelAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        newsReelRecyclerView.adapter = newsReelAdapter

        val topicName = topic.topic
        if (topicName != null) {
            // Get stored articles from the local database and display them
            // If there are no articles for the topic (in the case of no internet, or max API requests reached)
            // the no articles message is shown
            articleViewModel.getArticlesByTopic(topicName).observe(viewLifecycleOwner) {
                newsReelAdapter.submitList(it)

                if (newsReelAdapter.itemCount == 0) {
                    loadingFrame.visibility = View.GONE
                    statusText.visibility = View.VISIBLE
                } else {
                    loadingFrame.visibility = View.GONE
                    newsReelRecyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Start an implicit intent to open the article in a NewsArticleActivity
     */
    private fun onArticleClicked(article: Article, topic: Topic) {
        val intent = Intent(context, NewsArticleActivity::class.java)
        intent.putExtra("articleTopic", topic.topic)
        intent.putExtra("articleTitle", article.title)
        intent.putExtra("articleImageUrl", article.imageUrl)
        intent.putExtra("articleUrl", article.url)
        intent.putExtra("articleContent", article.content)
        intent.putExtra("articleAuthorName", article.sourceName)
        intent.putExtra("articleAuthorUrl", article.sourceUrl)
        startActivity(intent)
    }
}