package com.bukitvista.gros.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bukitvista.gros.R
import com.bukitvista.gros.databinding.ActivityProfileBinding
import com.bukitvista.gros.response.StaffResponse
import com.bukitvista.gros.retrofit.ApiConfig
import com.bukitvista.gros.sharedpreferences.SharedPreferencesManager
import com.bukitvista.gros.viewmodel.ProfileViewModel
import com.bukitvista.gros.viewmodel.RequestListViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    lateinit var viewModel: ProfileViewModel

    companion object {
        private const val TAG = "ProfileActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        fetchStaffData(SharedPreferencesManager.getStaffId(this))

        binding.ivBack.setOnClickListener {
            finish()
        }
        viewModel.getStaffData().observe(this) { staffData ->
            submitStaffData(staffData)
        }

        binding.btLogout.setOnClickListener {
            SharedPreferencesManager.clearStaffId(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun fetchStaffData(staffId: String?){
        showLoading(true)
        val client = staffId?.let { ApiConfig.getApiService().getStaffById(it) }
        client?.enqueue(object : Callback<StaffResponse> {
            override fun onResponse(
                call: Call<StaffResponse>, response: Response<StaffResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        viewModel.setStaffData(responseBody)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.body()}")
//                    Toast.makeText(this@MainActivity, "${response.code()}-${response.message()}", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@ProfileActivity, "Staff ID not found", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@ProfileActivity, errorMessage, Toast.LENGTH_SHORT).show()

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

    private fun submitStaffData(responseBody: StaffResponse) {
        binding.tvName.text = responseBody.name
        binding.tvStaffId.text = "STAFF ID : ${responseBody.id.toString()}"
        binding.tvPropertyId.text = "PROPERTY ID : ${responseBody.propertyId.toString()}"
        binding.tvRequestHandled.text = responseBody.requestHandled.toString()
    }
}