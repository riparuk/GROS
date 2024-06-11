package com.bukitvista.gros.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bukitvista.gros.R
import com.bukitvista.gros.adapter.RequestListAdapter
import com.bukitvista.gros.data.RequestItem
import com.bukitvista.gros.databinding.ActivityOnSiteRequestsBinding


class OnSiteRequestsActivity : AppCompatActivity(), RequestListAdapter.OnItemClickListener {
    lateinit var binding: ActivityOnSiteRequestsBinding

    private lateinit var adapter: RequestListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnSiteRequestsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // assigning ID of the toolbar to a variable
        val toolbar = binding.toolbar

        // Init adapter anda recycler view
        adapter = RequestListAdapter(this)
        binding.rvRequests.layoutManager = LinearLayoutManager(this)
        binding.rvRequests.addItemDecoration(DividerItemDecoration(this, (binding.rvRequests.layoutManager as LinearLayoutManager).getOrientation())) // Adjust color and height as needed

        // set request data list
        setRequestList(generateDummyData())

        // using toolbar as ActionBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initToolbarButtons()
        initControlDateButton()
        initCategoryTextViews()
    }

    private fun initToolbarButtons(){
        binding.ivSearch.setOnClickListener {
            Toast.makeText(this, "Search button clicked", Toast.LENGTH_SHORT).show()
        }

        binding.ivProfile.setOnClickListener {
            Toast.makeText(this, "Profile button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initControlDateButton() {
        // control date button
        binding.btSelectDate.setOnClickListener {
            // Handle button click event
            Toast.makeText(this, "Date Control button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCategoryTextViews() {
        val layoutCategories = binding.layoutCategories

        val textViews = listOf(
            layoutCategories.findViewById<TextView>(R.id.tvMyTasks),
            layoutCategories.findViewById<TextView>(R.id.tvAll),
            layoutCategories.findViewById<TextView>(R.id.tvUrgent),
            layoutCategories.findViewById<TextView>(R.id.tvImportant),
            layoutCategories.findViewById<TextView>(R.id.tvNormal)
        )

        for (textView in textViews) {
            textView.setOnClickListener {
                val category = textView.text.toString()
                Toast.makeText(this, "$category clicked", Toast.LENGTH_SHORT).show()
                // Perform any other actions or modifications
            }
        }
    }

    override fun onItemClick(item: RequestItem) {
        Toast.makeText(this, "Item : ${item.guestName} clicked", Toast.LENGTH_SHORT).show()
    }

    private fun setRequestList(items: List<RequestItem>) {
        adapter.submitList(items)
        binding.rvRequests.adapter = adapter
    }

    fun generateDummyData(): List<RequestItem> {
        return listOf(
            RequestItem(
                timestamp = "26-April-2025 14:30",
                guestName = "Laura Moren",
                description = "Laura is inquiring about the absence of the pick-up for the Nusa Penida West Tour. She is politely asking if there was a delay or any issues regarding the transportation. She is seeking clarification on the situation.",
                priority = "Important",
                progress = "1/3 Done",
                guestId = "1"
            ),
            RequestItem(
                timestamp = "27-April-2025 10:00",
                guestName = "John Doe",
                description = "John has reported an issue with the hotel booking system. He needs assistance with confirming his reservation and ensuring there are no overlaps.",
                priority = "Urgent",
                progress = "2/3 Done",
                guestId = "2"
            ),
            RequestItem(
                timestamp = "28-April-2025 08:45",
                guestName = "Jane Smith",
                description = "Jane is requesting information about the new tour packages available for the summer season. She is particularly interested in family-friendly options.",
                priority = "Normal",
                progress = "Completed",
                guestId = "1"
            )
        )
    }

}