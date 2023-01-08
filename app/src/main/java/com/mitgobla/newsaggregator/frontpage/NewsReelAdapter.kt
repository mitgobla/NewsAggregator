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
import com.mitgobla.newsaggregator.database.Article
import com.mitgobla.newsaggregator.topics.Topic

/**
 * Adapter for the RecyclerView in the NewsReelFragment.
 */
class NewsReelAdapter(private var topic: Topic, private var clickListener: (Article, Topic) -> Unit) : ListAdapter<Article, NewsReelAdapter.NewsReelViewHolder>(NewsReelComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsReelViewHolder {
        return NewsReelViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NewsReelViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(position, current, topic, clickListener)
    }

    /**
     * ViewHolder for the NewsReelAdapter, where the article data is bound to the elements in the
     * news item layout.
     */
    class NewsReelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val newsItemImageView: AppCompatImageView = itemView.findViewById(R.id.newsItemImageView)
        private val newsItemHeader: AppCompatTextView = itemView.findViewById(R.id.newsItemHeader)
        private val newsItemBrief: AppCompatTextView = itemView.findViewById(R.id.newsItemBrief)

        fun bind(position: Int, article: Article, topic: Topic,  clickListener: (Article, Topic) -> Unit) {
            itemView.setOnClickListener {
                clickListener.invoke(article, topic)
            }

            newsItemHeader.text = article.title
            newsItemHeader.contentDescription = article.title
            newsItemBrief.text = article.description
            newsItemBrief.contentDescription = article.description

            newsItemImageView.load(article.imageUrl) {
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

    /**
     * Compare if the items are the same, to avoid unnecessary updates
     */
    class NewsReelComparator : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }


}