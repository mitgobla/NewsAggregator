package com.mitgobla.newsaggregator.frontpage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.network.NewsApiInterface
import com.mitgobla.newsaggregator.network.NewsApiResponse
import com.mitgobla.newsaggregator.topics.Topic
import retrofit2.Call
import retrofit2.Callback

class NewsReelFragment(val topic: Topic) : Fragment(R.layout.fragment_news_reel) {

    var numberOfArticles = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val newsReelAdapter = NewsReelAdapter()
        val newsReelRecyclerView: RecyclerView = view.findViewById(R.id.newsReelRecyclerView)
        newsReelRecyclerView.adapter = newsReelAdapter

        val topicName = topic.topic

        val apiInterface: Call<NewsApiResponse> = if (topicName == "Breaking") {
            NewsApiInterface.create().getTopHeadlines(getString(R.string.gnews_token))
        } else {
            NewsApiInterface.create().searchByQuery(topicName, getString(R.string.gnews_token))
        }

        apiInterface.enqueue(object : Callback<NewsApiResponse> {
            override fun onResponse(
                call: Call<NewsApiResponse>,
                response: retrofit2.Response<NewsApiResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        newsReelAdapter.submitList(response.body()!!.articles)
                    }
                }
            }

            override fun onFailure(call: Call<NewsApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })



    }
}