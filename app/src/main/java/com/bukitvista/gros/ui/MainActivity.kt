package com.bukitvista.gros.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bukitvista.gros.R
import com.bukitvista.gros.databinding.ActivityMainBinding
import com.bukitvista.gros.response.RequestsResponseItem
import com.bukitvista.gros.response.StaffResponse
import com.bukitvista.gros.retrofit.ApiConfig
import com.bukitvista.gros.sharedpreferences.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding


    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (SharedPreferencesManager.getStaffId(this) != null){
            startActivity(Intent(this, OnSiteRequestsActivity::class.java))
            finish()
        }

        binding.btEnter.setOnClickListener {
            hideKeyboard()
            val staffId = binding.itStaffid.text.toString()
            if (staffId.isEmpty()) {
                binding.itStaffid.error = "Staff ID is required"
                return@setOnClickListener
            } else {
                binding.itStaffid.error = null
            }
            checkStaffId(staffId)
        }

    }

    fun checkStaffId(staffId: String){
        showLoading(true)
        val client = ApiConfig.getApiService().getStaffById(staffId)
        client.enqueue(object : Callback<StaffResponse> {
            override fun onResponse(
                call: Call<StaffResponse>, response: Response<StaffResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.propertyId != null){
                            SharedPreferencesManager.savePropertyId(this@MainActivity, responseBody.propertyId)
                            SharedPreferencesManager.saveStaffId(this@MainActivity, staffId)
                            startActivity(Intent(this@MainActivity, OnSiteRequestsActivity::class.java))
                        } else {
                            Toast.makeText(this@MainActivity, "Staff ID not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.body()}")
//                    Toast.makeText(this@MainActivity, "${response.code()}-${response.message()}", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@MainActivity, "Staff ID not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StaffResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")

                val errorMessage = if (t is IOException) {
                    // Jika kesalahan adalah IOException, artinya ada masalah dengan koneksi internet
                    "Problem with Connection"
                } else {
                    // Jika kesalahan bukan IOException, Anda dapat menampilkan pesan kesalahan yang diterima
                    t.message ?: "Something error"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()

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

    fun AppCompatActivity.hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}