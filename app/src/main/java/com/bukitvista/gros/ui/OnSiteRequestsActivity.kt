package com.bukitvista.gros.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bukitvista.gros.R
import com.bukitvista.gros.adapter.RequestListAdapter
import com.bukitvista.gros.data.RequestItem
import com.bukitvista.gros.databinding.ActivityOnSiteRequestsBinding


class OnSiteRequestsActivity : AppCompatActivity() {
    lateinit var binding: ActivityOnSiteRequestsBinding

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

        val dummyData = generateDummyData()

        val requestListAdapter = RequestListAdapter(dummyData)
        val recyclerView: RecyclerView = binding.rvRequests
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter  = requestListAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, (recyclerView.layoutManager as LinearLayoutManager).getOrientation())) // Adjust color and height as needed

        // using toolbar as ActionBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun generateDummyData(): List<RequestItem> {
        return listOf(
            RequestItem(
                timestamp = "26-April-2025 14:30",
                guestName = "Laura Moren",
                description = "Laura is inquiring about the absence of the pick-up for the Nusa Penida West Tour. She is politely asking if there was a delay or any issues regarding the transportation. She is seeking clarification on the situation.",
                priority = "Important",
                progress = "1/3 Done"
            ),
            RequestItem(
                timestamp = "27-April-2025 10:00",
                guestName = "John Doe",
                description = "John has reported an issue with the hotel booking system. He needs assistance with confirming his reservation and ensuring there are no overlaps.",
                priority = "Urgent",
                progress = "2/3 Done"
            ),
            RequestItem(
                timestamp = "28-April-2025 08:45",
                guestName = "Jane Smith",
                description = "Jane is requesting information about the new tour packages available for the summer season. She is particularly interested in family-friendly options.",
                priority = "Normal",
                progress = "Completed"
            )
        )
    }

}