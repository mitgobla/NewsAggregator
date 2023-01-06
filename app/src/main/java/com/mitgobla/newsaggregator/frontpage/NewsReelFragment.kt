package com.mitgobla.newsaggregator.frontpage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newsReelAdapter = NewsReelAdapter()
        val newsReelRecyclerView: RecyclerView = view.findViewById(R.id.newsReelRecyclerView)
        newsReelRecyclerView.adapter = newsReelAdapter

        val topicName = topic.topic
        Toast.makeText(context, "Topic: $topicName", Toast.LENGTH_SHORT).show()
        if (topicName != null) {
            articleViewModel.getArticlesByTopic(topicName).observe(viewLifecycleOwner) {
                newsReelAdapter.submitList(it)
            }
            newsReelAdapter.notifyDataSetChanged()
        }

    }
}