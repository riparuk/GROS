package com.bukitvista.gros.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    lateinit var viewModel: ProfileViewModel

    companion object {
        private const val TAG = "ProfileActivity"
        private const val PICK_IMAGE_REQUEST = 1
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

        binding.ivProfile.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data!!
            Glide.with(this).load(selectedImageUri).into(binding.ivProfile)
            uploadImageToServer(selectedImageUri)
        }
    }

    private fun uploadImageToServer(imageUri: Uri) {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(imageUri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val picturePath = cursor?.getString(columnIndex!!)
        cursor?.close()

        if (picturePath != null) {
            val file = File(picturePath)
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("profile_picture", file.name, requestBody)

            val apiService = ApiConfig.getApiService()
            val call = apiService.uploadProfilePicture(viewModel.getStaffData().value?.id.toString(), body)
            call.enqueue(object : Callback<StaffResponse> {
                override fun onResponse(call: Call<StaffResponse>, response: Response<StaffResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProfileActivity, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Upload failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StaffResponse>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Upload failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
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

        if (responseBody.photo == null) {
            binding.ivProfile.setImageResource(R.drawable.ic_add_photo_foreground)
        } else {
            Glide.with(this)
                .load(responseBody.photo.url)
                .into(binding.ivProfile)
        }

    }
}