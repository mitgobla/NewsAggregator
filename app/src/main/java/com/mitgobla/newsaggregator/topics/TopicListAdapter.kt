package com.mitgobla.newsaggregator.topics

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R

class TopicListAdapter(private val topicStateListener: (Topic) -> Unit) : ListAdapter<Topic, TopicListAdapter.TopicViewHolder>(TopicComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.topic, current.favourite, current.notify)

        holder.topicItemNotifyView.setOnCheckedChangeListener { _, state ->
            current.notify = state
            topicStateListener(current)
        }

        holder.topicItemFavouriteView.setOnCheckedChangeListener { _, state ->
            current.favourite = state
            topicStateListener(current)
        }
    }

    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val topicItemTextView: AppCompatTextView = itemView.findViewById(R.id.topicItemText)
        val topicItemFavouriteView: AppCompatCheckBox = itemView.findViewById(R.id.topicItemStar)
        val topicItemNotifyView: AppCompatCheckBox = itemView.findViewById(R.id.topicItemNotify)

        fun bind(text: String?, favourite: Boolean?, notify: Boolean?) {
            topicItemTextView.text = text
            if (favourite != null) {
                topicItemFavouriteView.isChecked = favourite
            }
            if (notify != null) {
                topicItemNotifyView.isChecked = notify
            }
        }

        companion object{
            fun create(parent: ViewGroup): TopicViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.topic_item, parent, false)
                return TopicViewHolder(view)
            }
        }
    }

    class TopicComparator : DiffUtil.ItemCallback<Topic>() {
        override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem.topic == newItem.topic && oldItem.favourite == newItem.favourite && oldItem.notify == newItem.notify
        }
    }
}