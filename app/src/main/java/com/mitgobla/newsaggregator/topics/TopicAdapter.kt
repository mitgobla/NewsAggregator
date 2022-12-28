package com.mitgobla.newsaggregator.topics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.LayoutInflaterCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R

class TopicAdapter(private val topicsList: List<TopicViewModel>) :
    RecyclerView.Adapter<TopicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.topic_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topicViewModel = topicsList[position]
        holder.topicTextView.text = topicViewModel.text
        holder.topicFavouriteButton.isChecked = topicViewModel.favourite
        holder.topicNotifyButton.isChecked = topicViewModel.notify
    }

    override fun getItemCount(): Int {
        return topicsList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val topicTextView: AppCompatTextView = itemView.findViewById(R.id.topicText)
        val topicFavouriteButton: AppCompatCheckBox = itemView.findViewById(R.id.buttonStar)
        val topicNotifyButton: AppCompatCheckBox = itemView.findViewById(R.id.toggleTopicNotifications)

    }
}