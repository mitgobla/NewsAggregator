package com.mitgobla.newsaggregator.topics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R

/**
 * Adapter for the list of topics RecyclerView in TopicsFragment.
 * Takes a click listener to update the topic when the favourite or notify toggle buttons are clicked.
 */
class TopicListAdapter(private var clickListener : ((String?, Boolean, Boolean) -> Unit)) : ListAdapter<Topic, TopicListAdapter.TopicViewHolder>(TopicComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.topic, current.favourite, current.notify, clickListener)
    }

    /**
     * ViewHolder for a topic that binds the data of the topic to their respective views
     * Binds the click listener to the favourite and notify toggle buttons
     */
    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val topicItemTextView: AppCompatTextView = itemView.findViewById(R.id.topicItemText)
        private val topicItemFavouriteView: AppCompatToggleButton = itemView.findViewById(R.id.topicItemStar)
        private val topicItemNotifyView: AppCompatToggleButton = itemView.findViewById(R.id.topicItemNotify)

        fun bind(text: String?, favourite: Boolean, notify: Boolean, clickListener: ((String?, Boolean, Boolean) -> Unit)) {
            topicItemTextView.text = text // Set the text to the topic
            topicItemTextView.contentDescription = text // Also set the content description to the topic, for accessibility
            topicItemFavouriteView.isChecked = favourite
            topicItemNotifyView.isChecked = notify
            topicItemNotifyView.isVisible = favourite // Only show the notify toggle if the topic is a favourite


            // Click Listener arguments
            // 1: String = Topic name
            // 2: Boolean = Favourite
            // 3: Boolean = Notify
            topicItemFavouriteView.setOnCheckedChangeListener { _, state ->
                clickListener.invoke(text, state, notify)
            }
            topicItemNotifyView.setOnCheckedChangeListener { _, state ->
                clickListener.invoke(text, favourite, state)
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

    /**
     * Filter the RecyclerView to only show topics that contain the search query
     */
    fun filter(query: String?) {
        val filteredList = mutableListOf<Topic>()
        if (query != null) {
            if (query.isNotBlank()) {
                for (topic in currentList) {
                    if (topic.topic?.contains(query, true) == true) {
                        filteredList.add(topic)
                    }
                }
                submitList(filteredList)
            } else {
                submitList(currentList)
            }
        } else {
            filteredList.addAll(currentList)
        }
        submitList(filteredList)
    }


    /**
     * Compare if the items are the same, to avoid unnecessary updates
     */
    class TopicComparator : DiffUtil.ItemCallback<Topic>() {
        override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem.topic == newItem.topic
        }

        override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem.topic == newItem.topic && oldItem.favourite == newItem.favourite && oldItem.notify == newItem.notify
        }
    }
}