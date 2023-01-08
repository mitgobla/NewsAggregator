package com.mitgobla.newsaggregator.auth

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.mitgobla.newsaggregator.R
import com.mitgobla.newsaggregator.database.Bookmark

/**
 * Adapter for the Bookmark [RecyclerView] in [UserFragment].
 */
class BookmarkListAdapter(private var clickListener: (Bookmark) -> Unit) : ListAdapter<Bookmark, BookmarkListAdapter.BookmarkViewHolder>(BookmarkComparator()) {

    /**
     * The ViewHolder constructor takes the binding variable from the associated
     * [Bookmark], which gives it access to the data.
     * It also gives the view clickable functionality, to open the bookmark.
     */
    class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val newsItemHeader = itemView.findViewById<AppCompatTextView>(R.id.newsItemHeader)
        private val newsItemImageView = itemView.findViewById<AppCompatImageView>(R.id.newsItemImageView)
        private val newsItemBrief = itemView.findViewById<AppCompatTextView>(R.id.newsItemBrief)

        fun bind(bookmark: Bookmark, clickListener: (Bookmark) -> Unit) {
            itemView.setOnClickListener {
                clickListener.invoke(bookmark)
            }

            newsItemHeader.text = bookmark.title
            newsItemHeader.contentDescription = bookmark.title
            newsItemBrief.text = bookmark.content
            newsItemBrief.contentDescription = bookmark.content

            newsItemImageView.load(bookmark.imageUrl) {
                crossfade(true)
                placeholder(ColorDrawable(itemView.context.getColor(R.color.loadingColor)))
                transformations(RoundedCornersTransformation(10f))
            }
        }

        companion object {
            fun create(parent: ViewGroup): BookmarkViewHolder {
                val view: View = android.view.LayoutInflater.from(parent.context)
                    .inflate(R.layout.news_item, parent, false)
                return BookmarkViewHolder(view)
            }
        }
    }

    /**
     * Compare if the items are the same, to avoid unnecessary updates
     */
    class BookmarkComparator : DiffUtil.ItemCallback<Bookmark>() {
        override fun areItemsTheSame(oldItem: Bookmark, newItem: Bookmark): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Bookmark, newItem: Bookmark): Boolean {
            return oldItem.url == newItem.url
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        return BookmarkViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, clickListener)
    }
}