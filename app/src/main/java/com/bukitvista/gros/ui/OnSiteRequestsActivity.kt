package com.bukitvista.gros.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bukitvista.gros.R
import com.bukitvista.gros.adapter.RequestListAdapter
import com.bukitvista.gros.data.Priority
import com.bukitvista.gros.data.RequestItem
import com.bukitvista.gros.databinding.ActivityOnSiteRequestsBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
        var startDate = Date(MaterialDatePicker.thisMonthInUtcMilliseconds())
        var endDate = Date(MaterialDatePicker.todayInUtcMilliseconds())
        MaterialDatePicker.todayInUtcMilliseconds()
        // control date button
        binding.btSelectDate.setOnClickListener {
            // Handle button click event
            showCalendar()
            Toast.makeText(this, "Date Control button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCategoryTextViews() {
        val layoutCategories = binding.layoutCategories

        val textViews = listOf<TextView>(
            layoutCategories.findViewById(R.id.tvMyTasks),
            layoutCategories.findViewById(R.id.tvAll),
            layoutCategories.findViewById(R.id.tvUrgent),
            layoutCategories.findViewById(R.id.tvImportant),
            layoutCategories.findViewById(R.id.tvNormal)
        )

        for (textView in textViews) {
            textView.setOnClickListener {
                for (view in layoutCategories.children) {
                    if (view is TextView) {
                        view.background = getDrawable(R.drawable.rounded_corner_white)
                        view.setTextColor(getColor(R.color.blue700))
                    }
                }
                textView.background = getDrawable(R.drawable.rounded_corner_blue)
                textView.setTextColor(getColor(R.color.white))
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

    private fun showCalendar() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setSelection(
                Pair(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .setTitleText("Select dates")
            .build()

        datePicker.addOnPositiveButtonClickListener { dateRange ->
            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.tvStartDate.text = format.format(dateRange.first)
            binding.tvEndDate.text = format.format(dateRange.second)
            // implement re-run fetch api for selected dates here
        }

        datePicker.show(supportFragmentManager, "DateRangePicker")
    }

    fun String.toDate(): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.parse(this)
    }

    fun generateDummyData(): List<RequestItem> {
        return listOf(
            RequestItem(
                timestamp = "2024-05-20 05:01:58",
                guestId = "HMKSAZCEHK",
                guestName = "Libby",
                description = "Libby is waiting for assistance at the property front office to store her bags and wants to know if someone is on their way to help.",
                priority = Priority.HIGH,
                progress = "1/3 Done"
            ),
            RequestItem(
                timestamp = "2024-05-20 04:31:47",
                guestId = "HMKSAZCEHK",
                guestName = "Libby",
                description = "Libby is asking for information about where to store her bags and whether the reception desk is the same as the front office.",
                priority = Priority.HIGH,
                progress = "2/3 Done"
            ),
            RequestItem(
                timestamp = "2024-05-21 06:03:56",
                guestId = "HMEMRAR2XJ",
                guestName = "Nadya",
                description = "The guest is inquiring about the availability of house cleaning service and requesting new towels in the property.",
                priority = Priority.HIGH,
                progress = "Completed"
            ),
            RequestItem(
                timestamp = "2024-05-21 06:33:21",
                guestId = "HMSMXNFMRP",
                guestName = "Ollie",
                description = "Ollie reported an issue with the drainage in the shower tray. The water level rises and drains slowly, posing a risk of flooding in the bathroom.",
                priority = Priority.HIGH,
                progress = "1/3 Done"
            ),
            RequestItem(
                timestamp = "2024-05-15 19:26:19",
                guestId = "4617255719",
                guestName = "Ricardo",
                description = "Ricardo mentioned a request for a van pick-up and motorbike rentals. He might need assistance in confirming the availability and arranging these services.",
                priority = Priority.MEDIUM,
                progress = "2/3 Done"
            ),
            RequestItem(
                timestamp = "2024-05-07 02:52:17",
                guestId = "HMER4XA3PF",
                guestName = "Evan",
                description = "User offers to provide any additional information required for the pick-up arrangements, showing willingness to cooperate.",
                priority = Priority.MEDIUM,
                progress = "Completed"
            ),
            RequestItem(
                timestamp = "2024-05-19 07:05:34",
                guestId = "HMEREZT9ZH",
                guestName = "Beth",
                description = "Beth reported that a fuse has tripped in one of the rooms at the villa and requested assistance in locating the fuse box and resetting it.",
                priority = Priority.HIGH,
                progress = "2/3 Done"
            ),
            RequestItem(
                timestamp = "2024-05-19 12:42:42",
                guestId = "HM333NCXWN",
                guestName = "Mischa",
                description = "Mischa arrived at the villa but encountered an issue with the air conditioning in one of the rooms. The staff mentioned it can only be fixed tomorrow, to which Mischa expressed the necessity to have it fixed for the night.",
                priority = Priority.HIGH,
                progress = "1/3 Done"
            ),
            RequestItem(
                timestamp = "2024-04-28 02:41:37",
                guestId = "HMBQS2CB5X",
                guestName = "Georgia",
                description = "Georgia is requesting to leave the key and bags at the reception without an onsite staff present as they are ready to depart.",
                priority = Priority.LOW,
                progress = "Completed"
            ),
            RequestItem(
                timestamp = "2024-05-04 01:57:25",
                guestId = "HMTRPJSCCT",
                guestName = "Nadia",
                description = "Nadia asked if someone will be present to assist with the check-in process at the villa.",
                priority = Priority.MEDIUM,
                progress = "Completed"
            )
        )
    }


}