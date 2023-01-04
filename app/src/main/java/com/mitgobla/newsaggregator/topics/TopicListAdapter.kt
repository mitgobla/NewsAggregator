package com.mitgobla.newsaggregator.topics

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R

class TopicListAdapter(private var clickListener : ((Topic) -> Unit)) : ListAdapter<Topic, TopicListAdapter.TopicViewHolder>(TopicComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.id, current.topic, current.favourite, current.notify, clickListener)
    }

    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val topicItemTextView: AppCompatTextView = itemView.findViewById(R.id.topicItemText)
        private val topicItemFavouriteView: AppCompatCheckBox = itemView.findViewById(R.id.topicItemStar)
        private val topicItemNotifyView: AppCompatCheckBox = itemView.findViewById(R.id.topicItemNotify)

        fun bind(rowid: Int, text: String, favourite: Boolean, notify: Boolean, clickListener: ((Topic) -> Unit)) {
            topicItemTextView.text = text
            topicItemFavouriteView.isChecked = favourite
            topicItemNotifyView.isChecked = notify

            topicItemFavouriteView.setOnCheckedChangeListener { _, state ->
                clickListener.invoke(Topic(rowid, text, state, notify))
            }

            topicItemNotifyView.setOnCheckedChangeListener { _, state ->
                clickListener.invoke(Topic(rowid, text, favourite, state))
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
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem.topic == newItem.topic && oldItem.favourite == newItem.favourite && oldItem.notify == newItem.notify
        }
    }
}