package com.bukitvista.gros.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bukitvista.gros.R
import com.bukitvista.gros.databinding.ActivityProfileBinding
import com.bukitvista.gros.response.StaffResponse
import com.bukitvista.gros.retrofit.ApiConfig
import com.bukitvista.gros.sharedpreferences.SharedPreferencesManager
import com.bukitvista.gros.viewmodel.ProfileViewModel
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
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Izin diberikan, lanjutkan dengan logika Anda
            pickImage()
        } else {
            // Izin ditolak, berikan penjelasan kepada pengguna atau ambil tindakan lain sesuai kebutuhan
            Toast.makeText(this, "Permission denied to read media images", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data?.data
            selectedImageUri?.let { uri ->
                Glide.with(this).load(uri).into(binding.ivProfile)
                uploadImageToServer(uri)
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Izin sudah diberikan, lanjutkan dengan logika Anda
            pickImage()
        } else {
            // Meminta izin kepada pengguna
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
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
            checkAndRequestPermissions()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
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
            val contentType = getMimeType(file)

            val requestBody = file.asRequestBody(contentType.toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestBody)

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
                    Log.e(TAG, "onFailure: ${t.message}")
                    Toast.makeText(this@ProfileActivity, "Upload failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun getMimeType(file: File): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/*"
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
                    Toast.makeText(this@ProfileActivity, "Staff ID not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StaffResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")

                val errorMessage = if (t is IOException) {
                    "Problem with Connection"
                } else {
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
        binding.tvStaffId.text = "STAFF ID : ${responseBody.id}"
        binding.tvPropertyId.text = "PROPERTY ID : ${responseBody.propertyId}"
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
