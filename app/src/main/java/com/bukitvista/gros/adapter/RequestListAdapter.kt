package com.bukitvista.gros.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bukitvista.gros.R
import com.bukitvista.gros.data.RequestItem

class RequestListAdapter(private val dataSet: List<RequestItem>) :
    RecyclerView.Adapter<RequestListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
        val tvGuestName: TextView = view.findViewById(R.id.tvGuestName)
        val tvDescription: TextView = view.findViewById(R.id.textView)
        val tvPriority: TextView = view.findViewById(R.id.tvPriority)
        val tvProgress: TextView = view.findViewById(R.id.ivDone)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_request, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val item = dataSet[position]
        viewHolder.tvTimestamp.text = item.timestamp
        viewHolder.tvGuestName.text = item.guestName
        viewHolder.tvDescription.text = item.description
        viewHolder.tvProgress.text = item.progress

        if (item.priority == "Important") {
            viewHolder.tvPriority.background = (viewHolder.tvPriority.context.getDrawable(R.drawable.rounded_tag_yellow700))
        } else if (item.priority == "Urgent") {
            viewHolder.tvPriority.background = (viewHolder.tvPriority.context.getDrawable(R.drawable.rounded_tag_red700))
        } else if (item.priority == "Normal") {
            viewHolder.tvPriority.background = (viewHolder.tvPriority.context.getDrawable(R.drawable.rounded_tag_blue500))
        }
        viewHolder.tvPriority.text = item.priority


    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}