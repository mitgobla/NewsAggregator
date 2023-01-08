package com.mitgobla.newsaggregator.auth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mitgobla.newsaggregator.R

/**
 * Adapter for the metric [RecyclerView] in [UserFragment].
 * Takes a maximum value, which is the highest count in the metrics. This way the progress
 * bars will be scaled to the most read topic.
 */
class MetricListAdapter(private val maxValue: Int) : ListAdapter<Metric, MetricListAdapter.MetricViewHolder>(MetricComparator()) {

    /**
     * Takes data from the [Metric] and binds it to the views.
     */
    class MetricViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val metricName = itemView.findViewById<AppCompatTextView>(R.id.metricName)
        private val metricProgress = itemView.findViewById<ProgressBar>(R.id.metricProgress)

        fun bind(metric: Metric, maxValue: Int) {
            val text = itemView.context.getString(R.string.metricName, metric.topic, metric.count)
            metricName.text = text
            metricName.contentDescription = text
            metricProgress.max = maxValue
            metricProgress.progress = metric.count
        }

        companion object {
            fun create(parent: ViewGroup): MetricViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.metric_item, parent, false)
                return MetricViewHolder(view)
            }
        }
    }

    /**
     * Compare if the items are the same, to avoid unnecessary updates
     */
    class MetricComparator : DiffUtil.ItemCallback<Metric>() {
        override fun areItemsTheSame(oldItem: Metric, newItem: Metric): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Metric, newItem: Metric): Boolean {
            return oldItem.topic == newItem.topic
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetricViewHolder {
        return MetricViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MetricViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, maxValue)
    }

}