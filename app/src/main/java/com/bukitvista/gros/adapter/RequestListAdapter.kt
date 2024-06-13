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
import com.bukitvista.gros.response.RequestsResponse
import com.bukitvista.gros.response.RequestsResponseItem

class RequestListAdapter(private val listener: OnItemClickListener)
    :  ListAdapter<RequestsResponseItem, RequestListAdapter.ViewHolder>(DIFF_CALLBACK) {

        interface OnItemClickListener {
            fun onItemClick(item: RequestsResponseItem)
        }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(private val binding: ItemRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RequestsResponseItem) {
            binding.tvTimestamp.text = item.createdAt
            binding.tvGuestName.text = item.guestName
            binding.tvDescription.text = item.description

            // Count progress done
            val progressCount = listOf(
                item.receiveVerifyCompleted,
                item.coordinateActionCompleted,
                item.followUpResolveCompleted
            )
            val progressDone = progressCount.count { it == true }
            val progressTotal = progressCount.size
            val progress = "$progressDone/$progressTotal"
            binding.tvProgress.text = progress

            if (item.priority == 4) {
                binding.tvPriority.background = (binding.tvPriority.context.getDrawable(R.drawable.rounded_tag_red700))
            } else if (item.priority == 3) {
                binding.tvPriority.background = (binding.tvPriority.context.getDrawable(R.drawable.rounded_tag_yellow700))
            } else if (item.priority == 2) {
                binding.tvPriority.background = (binding.tvPriority.context.getDrawable(R.drawable.rounded_tag_blue500))
            } else if (item.priority == 1) {
                binding.tvPriority.background = (binding.tvPriority.context.getDrawable(R.drawable.rounded_tag_gray700))
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RequestsResponseItem>() {
            override fun areItemsTheSame(oldItem: RequestsResponseItem, newItem: RequestsResponseItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: RequestsResponseItem, newItem: RequestsResponseItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}