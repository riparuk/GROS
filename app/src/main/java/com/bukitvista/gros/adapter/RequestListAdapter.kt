package com.bukitvista.gros.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bukitvista.gros.R
import com.bukitvista.gros.data.RequestItem
import com.bukitvista.gros.databinding.ItemRequestBinding

class RequestListAdapter(private val listener: OnItemClickListener)
    :  ListAdapter<RequestItem, RequestListAdapter.ViewHolder>(DIFF_CALLBACK) {

        interface OnItemClickListener {
            fun onItemClick(item: RequestItem)
        }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(private val binding: ItemRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RequestItem) {
            binding.tvTimestamp.text = item.timestamp
            binding.tvGuestName.text = item.guestName
            binding.tvDescription.text = item.description
            binding.tvProgress.text = item.progress

            if (item.priority == 4.0) {
                binding.tvPriority.background = (binding.tvPriority.context.getDrawable(R.drawable.rounded_tag_red700))
            } else if (item.priority == 3.0) {
                binding.tvPriority.background = (binding.tvPriority.context.getDrawable(R.drawable.rounded_tag_yellow700))
            } else if (item.priority == 2.0) {
                binding.tvPriority.background = (binding.tvPriority.context.getDrawable(R.drawable.rounded_tag_blue500))
            } else if (item.priority == 1.0) {
                binding.tvPriority.background =
                    (binding.tvPriority.context.getDrawable(R.drawable.rounded_tag_blue100))
            }
                binding.tvPriority.text = item.priority.toString()
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val binding = ItemRequestBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val item = getItem(position)
        viewHolder.bind(item)
        viewHolder.itemView.setOnClickListener {
            listener.onItemClick(item)
        }


    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RequestItem>() {
            override fun areItemsTheSame(oldItem: RequestItem, newItem: RequestItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: RequestItem, newItem: RequestItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}