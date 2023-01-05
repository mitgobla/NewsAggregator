package com.mitgobla.newsaggregator.frontpage

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.network.ArticleResponse
import com.mitgobla.newsaggregator.network.NewsApiResponse

class NewsReelAdapter : ListAdapter<ArticleResponse, NewsReelAdapter.NewsReelViewHolder>(NewsReelComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsReelViewHolder {
        return NewsReelViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NewsReelViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(position, current)
    }

    class NewsReelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val newsItemImageView: AppCompatImageView = itemView.findViewById(R.id.newsItemImageView)
        private val newsItemHeader: AppCompatTextView = itemView.findViewById(R.id.newsItemHeader)
        private val newsItemBrief: AppCompatTextView = itemView.findViewById(R.id.newsItemBrief)

        fun bind(position: Int, articleResponse: ArticleResponse) {
            newsItemHeader.text = articleResponse.title
            newsItemHeader.contentDescription = articleResponse.title
            newsItemBrief.text = articleResponse.description
            newsItemBrief.contentDescription = articleResponse.description

            newsItemImageView.load(articleResponse.imageUrl) {
                crossfade(true)
                placeholder(ColorDrawable(getColor(itemView.context, R.color.loadingColor)))
                transformations(RoundedCornersTransformation(10f))
            }
        }

        companion object {
            fun create(parent: ViewGroup): NewsReelViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.news_item, parent, false)
                return NewsReelViewHolder(view)
            }
        }
    }

    class NewsReelComparator : DiffUtil.ItemCallback<ArticleResponse>() {
        override fun areItemsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
            return oldItem == newItem
        }
    }


}