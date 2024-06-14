package com.bukitvista.gros.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bukitvista.gros.R
import com.bukitvista.gros.adapter.RequestListAdapter
import com.bukitvista.gros.data.ImageItem
import com.bukitvista.gros.data.RequestItem
import com.bukitvista.gros.databinding.ActivityOnSiteRequestsBinding
import com.bukitvista.gros.response.RequestsResponseItem
import com.bukitvista.gros.retrofit.ApiConfig
import com.bukitvista.gros.sharedpreferences.SharedPreferencesManager
import com.bukitvista.gros.viewmodel.FilterViewModel
import com.bukitvista.gros.viewmodel.RequestListViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class OnSiteRequestsActivity : AppCompatActivity(), RequestListAdapter.OnItemClickListener {
    lateinit var binding: ActivityOnSiteRequestsBinding
    lateinit var viewModel: RequestListViewModel
    lateinit var filterViewModel: FilterViewModel
    private lateinit var adapter: RequestListAdapter

    companion object {
        private const val TAG = "OnSiteRequestActivity"
        var STAFF_ID: String? = null.toString()
        var PROPERTY_ID: String = null.toString()
    }

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

        STAFF_ID = SharedPreferencesManager.getStaffId(this)
        PROPERTY_ID = SharedPreferencesManager.getPropertyId(this)

        // Jika belum login
        if (STAFF_ID == null) {
            // Lakukan sesuatu dengan storedStaffId
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        viewModel = ViewModelProvider(this)[RequestListViewModel::class.java]
        filterViewModel = ViewModelProvider(this)[FilterViewModel::class.java]

        // assigning ID of the toolbar to a variable
        val toolbar = binding.toolbar

        // Init adapter anda recycler view
        adapter = RequestListAdapter(this)
        binding.rvRequests.layoutManager = LinearLayoutManager(this)
        binding.rvRequests.addItemDecoration(DividerItemDecoration(this, (binding.rvRequests.layoutManager as LinearLayoutManager).getOrientation())) // Adjust color and height as needed

        binding.rvRequests.adapter = adapter

        viewModel.requestList.observe(this, Observer { items ->
            setRequestList(items)
        })

        filterViewModel.filters.observe(this, Observer { filters ->
            fetchRequests()
        })
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initToolbarButtons()
        initControlDateSetup()
        initCategoryTextViews()
    }

    fun fetchRequests() {
        showLoading(true)
        // Convert date to query support date format
        val startDateFormatForQuery = SimpleDateFormat("yyyy-MM-dd'T00:00:00'", Locale.getDefault())
        val endDateFormatForQuery = SimpleDateFormat("yyyy-MM-dd'T23:59:00'", Locale.getDefault())
        val startDate = filterViewModel.filters.value?.startDate?.let { startDateFormatForQuery.format(it) }
        val endDate = filterViewModel.filters.value?.endDate?.let { endDateFormatForQuery.format(it) }
        val client = ApiConfig.getApiService().getRequests(propertyId = PROPERTY_ID, startDate = startDate, endDate = endDate, priority = filterViewModel.filters.value?.priority, assignTo = filterViewModel.filters.value?.assignTo)
        client.enqueue(object : Callback<List<RequestsResponseItem>> {
            override fun onResponse(
                call: Call<List<RequestsResponseItem>>, response: Response<List<RequestsResponseItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        viewModel.setItems(responseBody)
                    }
                } else {
                    Log.e(TAG, "onFailure: $response")
                    Toast.makeText(this@OnSiteRequestsActivity, "Error:${response.code()}-${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RequestsResponseItem>>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")

                val errorMessage = if (t is IOException) {
                    // Jika kesalahan adalah IOException, artinya ada masalah dengan koneksi internet
                    "Problem with Connection"
                } else {
                    // Jika kesalahan bukan IOException, Anda dapat menampilkan pesan kesalahan yang diterima
                    t.message ?: "Something error"
                }

                Toast.makeText(this@OnSiteRequestsActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun initToolbarButtons(){

        binding.ivSearch.setOnClickListener {
            Toast.makeText(this, "Search button in Development!", Toast.LENGTH_SHORT).show()
        }

        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun initControlDateSetup() {
        // Format tanggal
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        val start = Date(MaterialDatePicker.thisMonthInUtcMilliseconds())
        val end = Date(MaterialDatePicker.todayInUtcMilliseconds())
        filterViewModel.updateRangeDate(start, end)

        // Dapatkan tanggal dalam milliseconds dan format menjadi string
        val startDate = filterViewModel.filters.value?.startDate
        val endDate = filterViewModel.filters.value?.endDate

        // Set text pada TextView
        binding.tvStartDate.text = startDate?.let { dateFormat.format(it) }
        binding.tvEndDate.text = endDate?.let { dateFormat.format(it) }
        // control date button
        binding.btSelectDate.setOnClickListener {
            // Handle button click event
            showCalendar()
//            Toast.makeText(this, "Date Control button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCategoryTextViews() {
        val layoutCategories = binding.layoutCategories

        val textViews = listOf(
            binding.tvMyTasks,
            binding.tvAll,
            binding.tvUrgent,
            binding.tvImportant,
            binding.tvNormal,
            binding.tvLow,
        )

        if (filterViewModel.filters.value?.activeTag != null) {
            textViews[filterViewModel.filters.value?.activeTag!!].background = getDrawable(R.drawable.rounded_corner_blue)
            textViews[filterViewModel.filters.value?.activeTag!!].setTextColor(getColor(R.color.white))
        }

        for (textView in textViews) {
            textView.setOnClickListener {
                for (view in layoutCategories.children) {
                    if (view is TextView) {
                        view.background = getDrawable(R.drawable.rounded_corner_white)
                        view.setTextColor(getColor(R.color.blue700))
                    }
                }
                // Set background and text color for the clicked TextView
                textView.background = getDrawable(R.drawable.rounded_corner_blue)
                textView.setTextColor(getColor(R.color.white))
                val category = textView.text.toString()
                if(category == "My Tasks"){
                    filterViewModel.update(priority = null, assignTo = STAFF_ID, activeTag = 0)
                } else if (category == "All"){
                    filterViewModel.update(priority = null, assignTo = null, activeTag = 1)
                } else if(category == "Urgent"){
                    filterViewModel.update(priority = 4, assignTo = null, activeTag = 2)
                } else if(category == "Important"){
                    filterViewModel.update(priority = 3, assignTo = null, activeTag = 3)
                } else if(category == "Normal"){
                    filterViewModel.update(priority = 2, assignTo = null, activeTag = 4)
                } else if(category == "Low"){
                    filterViewModel.update(priority = 1, assignTo = null, activeTag = 5)
                }
//                Toast.makeText(this, "$category clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemClick(item: RequestsResponseItem) {
        Toast.makeText(this, "Item : ${item.guestName} clicked", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, DetailRequestActivity::class.java)
        intent.putExtra("REQUEST_ITEM", item)
        startActivity(intent)
    }

    private fun setRequestList(requests: List<RequestsResponseItem>) {
        adapter.submitList(requests)
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
            val simple = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.tvStartDate.text = simple.format(dateRange.first)
            binding.tvEndDate.text = simple.format(dateRange.second)
            filterViewModel.update(startDate = Date(dateRange.first), endDate = Date(dateRange.second))
            // implement re-run fetch api for selected dates here
        }

        datePicker.show(supportFragmentManager, "DateRangePicker")
    }

}