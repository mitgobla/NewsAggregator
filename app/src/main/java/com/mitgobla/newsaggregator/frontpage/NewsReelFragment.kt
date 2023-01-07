package com.mitgobla.newsaggregator.frontpage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.database.Article
import com.mitgobla.newsaggregator.database.ArticleApplication
import com.mitgobla.newsaggregator.database.ArticleViewModel
import com.mitgobla.newsaggregator.database.ArticleViewModelFactory
import com.mitgobla.newsaggregator.network.NewsApiInterface
import com.mitgobla.newsaggregator.network.NewsApiResponse
import com.mitgobla.newsaggregator.topics.Topic
import retrofit2.Call
import retrofit2.Callback

class NewsReelFragment(val topic: Topic) : Fragment(R.layout.fragment_news_reel) {

    private val articleViewModel: ArticleViewModel by viewModels() {
        ArticleViewModelFactory((activity?.application as ArticleApplication).repository)
    }

    lateinit var newsReelRecyclerView: RecyclerView
    private lateinit var newsReelAdapter: NewsReelAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("NewsReelFragment", "onAttach: ${topic.topic}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsReelAdapter = NewsReelAdapter(topic) { article, topic ->
            onArticleClicked(article, topic)
        }
        newsReelAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        newsReelRecyclerView = view.findViewById(R.id.newsReelRecyclerView)
        newsReelRecyclerView.layoutManager = LinearLayoutManager(context)
        newsReelRecyclerView.adapter = newsReelAdapter

        val topicName = topic.topic
        if (topicName != null) {
            articleViewModel.getArticlesByTopic(topicName).observe(viewLifecycleOwner) {
                newsReelAdapter.submitList(it)
            }
        }
        Log.i("NewsReelFragment", "number of articles: ${newsReelAdapter.itemCount} for topic $topicName")
    }

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