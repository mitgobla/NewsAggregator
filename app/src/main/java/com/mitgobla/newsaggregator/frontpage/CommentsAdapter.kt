package com.mitgobla.newsaggregator.frontpage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R

class CommentsAdapter : ListAdapter<Comment,CommentsAdapter.CommentsViewHolder>(CommentComparator()) {

    class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val commentAuthor: AppCompatTextView = itemView.findViewById(R.id.commentAuthor)
        private val commentContent: AppCompatTextView = itemView.findViewById(R.id.commentContent)
        fun bind(comment: Comment) {
            val commentAuthorString = itemView.context.getString(R.string.commentAuthor, comment.by)
            commentAuthor.text = commentAuthorString
            commentAuthor.contentDescription = commentAuthorString
            commentContent.text = comment.comment
            commentContent.contentDescription = comment.comment
        }

        companion object {
            fun create(parent: ViewGroup): CommentsAdapter.CommentsViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.comment_item, parent, false)
                return CommentsAdapter.CommentsViewHolder(view)
            }
        }
    }

    class CommentComparator : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.comment == newItem.comment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }
}