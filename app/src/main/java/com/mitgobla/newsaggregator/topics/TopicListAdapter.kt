package com.mitgobla.newsaggregator.topics

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.view.isVisible
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
        holder.bind(current.id, current.topic, current.favourite, current.notify, current.readCount, current.required, clickListener)

        holder.topicItemFavouriteView.isEnabled = !current.required
    }

    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val topicItemTextView: AppCompatTextView = itemView.findViewById(R.id.topicItemText)
        val topicItemFavouriteView: AppCompatToggleButton = itemView.findViewById(R.id.topicItemStar)
        private val topicItemNotifyView: AppCompatToggleButton = itemView.findViewById(R.id.topicItemNotify)

        fun bind(rowid: Int, text: String, favourite: Boolean, notify: Boolean, readCount: Int, required: Boolean, clickListener: ((Topic) -> Unit)) {
            topicItemTextView.text = text // Set the text to the topic
            topicItemTextView.contentDescription = text // Also set the content description to the topic, for accessibility
            topicItemFavouriteView.isChecked = favourite
            topicItemNotifyView.isChecked = notify


            topicItemFavouriteView.setOnCheckedChangeListener { _, state ->
                clickListener.invoke(Topic(rowid, text, state, notify, readCount, required))
            }
            topicItemNotifyView.setOnCheckedChangeListener { _, state ->
                clickListener.invoke(Topic(rowid, text, favourite, state, readCount, required))
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