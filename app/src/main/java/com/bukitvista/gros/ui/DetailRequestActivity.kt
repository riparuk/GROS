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
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bukitvista.gros.R
import com.bukitvista.gros.databinding.ActivityDetailRequestBinding
import com.bukitvista.gros.response.ImageURLsItem
import com.bukitvista.gros.response.RequestsResponseItem
import com.bukitvista.gros.retrofit.ApiConfig
import com.bukitvista.gros.sharedpreferences.SharedPreferencesManager
import com.bukitvista.gros.viewmodel.RequestItemViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class DetailRequestActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailRequestBinding
    lateinit var viewModel: RequestItemViewModel

    companion object {
        private const val TAG = "DetailRequestActivity"
        private const val PICK_IMAGE_REQUEST = 1
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Izin diberikan, lanjutkan dengan logika Anda
            pickImages()
        } else {
            // Izin ditolak, berikan penjelasan kepada pengguna atau ambil tindakan lain sesuai kebutuhan
            Toast.makeText(this, "Permission denied to read media images", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val clipData = result.data?.clipData
            clipData?.let { cd ->
                for (i in 0 until cd.itemCount) {
                    val imageUri: Uri? = cd.getItemAt(i).uri
                    imageUri?.let { uri ->
                        uploadImageToRequest(uri)
                    }
                }
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
            pickImages()
        } else {
            // Meminta izin kepada pengguna
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRequestBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this)[RequestItemViewModel::class.java]

        val staffId = SharedPreferencesManager.getStaffId(this)

        // Mendapatkan item dari intent
        val requestItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("REQUEST_ITEM", RequestsResponseItem::class.java)
        } else {
            intent.getParcelableExtra<RequestsResponseItem>("REQUEST_ITEM")
        }

        viewModel.setItem(requestItem)

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btAssign.setOnClickListener {
            assignRequest(requestItem?.id!!, staffId = staffId?.toInt()!!)
        }
        binding.btAddAttachments.setOnClickListener {
            checkAndRequestPermissions()
        }

        viewModel.getItem().observe(this) { it ->
            if(it?.assignTo != null) {
                binding.btAssign.isEnabled = false
                binding.btAssign.text = "Assigned"
                binding.btAssign.setBackgroundColor(ContextCompat.getColor(this, R.color.gray400))
            }
            setRequestData(it)
        }

    }

    private fun pickImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        pickImagesLauncher.launch(intent)
    }

    private fun getMimeType(file: File): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/*"
    }

    private fun uploadImageToRequest(imageUri: Uri) {
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
            val body = MultipartBody.Part.createFormData("files", file.name, requestBody)

            val apiService = ApiConfig.getApiService()
            viewModel.getItem().value?.id?.let { apiService.uploadRequestImage(it, body) }
                ?.enqueue(object : Callback<List<ImageURLsItem?>?> {
                    override fun onResponse(
                        call: Call<List<ImageURLsItem?>?>,
                        response: Response<List<ImageURLsItem?>?>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@DetailRequestActivity,
                                "Profile picture uploaded successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.e(TAG, "Upload failed: ${response.message()}")
                            Toast.makeText(
                                this@DetailRequestActivity,
                                "Upload failed: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<List<ImageURLsItem?>?>, t: Throwable) {
                        Toast.makeText(
                            this@DetailRequestActivity,
                            "Upload failed: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    fun fetchRequests() {
        showLoading(true)
        val client = ApiConfig.getApiService().getRequests(requestId = viewModel.getItem().value?.id)
        client.enqueue(object : Callback<List<RequestsResponseItem>> {
            override fun onResponse(
                call: Call<List<RequestsResponseItem>>, response: Response<List<RequestsResponseItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.isNotEmpty()) {
                        viewModel.setItem(responseBody[0])
                    }
                } else {
                    Log.e(TAG, "onFailure: $response")
                    Toast.makeText(this@DetailRequestActivity, "Error:${response.code()}-${response.message()}", Toast.LENGTH_SHORT).show()
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

                Toast.makeText(this@DetailRequestActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
//        if (isLoading) {
//            binding.progressBar.visibility = View.VISIBLE
//        } else {
//            binding.progressBar.visibility = View.GONE
//        }
    }

    fun updateRequestStep(requestId: Int, step: Int) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.updateRequestStep(requestId, step)

        call.enqueue(object : Callback<RequestsResponseItem> {
            override fun onResponse(call: Call<RequestsResponseItem>, response: Response<RequestsResponseItem>) {
                if (response.isSuccessful) {
                    val updatedRequest = response.body()
                    if (updatedRequest != null) {
                        viewModel.setItem(updatedRequest)
                        Toast.makeText(this@DetailRequestActivity, "Step request berhasil diupdate", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DetailRequestActivity, "Response body kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    viewModel.setItem(viewModel.getItem().value)
                    Toast.makeText(this@DetailRequestActivity, "Invalid step or previous steps not completed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RequestsResponseItem>, t: Throwable) {
                Toast.makeText(this@DetailRequestActivity, "Gagal update step request: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun assignRequest(requestId: Int, staffId: Int) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.assignRequest(requestId, staffId)

        call.enqueue(object : Callback<RequestsResponseItem> {
            override fun onResponse(call: Call<RequestsResponseItem>, response: Response<RequestsResponseItem>) {
                if (response.isSuccessful) {
                    val assignedRequest = response.body()
                    if (assignedRequest != null) {
                        viewModel.setItem(assignedRequest)
                        Toast.makeText(this@DetailRequestActivity, "Request berhasil diassign", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DetailRequestActivity, "Response body kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DetailRequestActivity, "Gagal assign request: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RequestsResponseItem>, t: Throwable) {
                Toast.makeText(this@DetailRequestActivity, "Gagal assign request: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setRequestData(requestItem: RequestsResponseItem?) {
        binding.tvSummary.text = requestItem?.description
        binding.tvPriority.text = requestItem?.priority.toString()
        binding.tvGuestName.text = requestItem?.guestName
        binding.tvTimestamp.text = requestItem?.createdAt
        binding.tvActions.text = requestItem?.actions
        binding.tvStaffName.text = requestItem?.staffName

        // Load staff profile image
//        Glide.with(this)
//            .load(requestItem?.staffImageURL?.url)
//            .apply(RequestOptions.circleCropTransform())
//            .into(binding.ivStaffProfile)

        val imageFiles: List<ImageURLsItem?>? = requestItem?.imageURLs

        // Count progress done
        val progressCount = listOf(
            requestItem?.receiveVerifyCompleted,
            requestItem?.coordinateActionCompleted,
            requestItem?.followUpResolveCompleted
        )
        val progressDone = progressCount.count { it == true }
        val progressTotal = progressCount.size
        val progress = "$progressDone/$progressTotal"
        binding.tvProgress.text = progress

        // Pastikan imageURLs tidak null dan tidak kosong sebelum mengisikan TextView
        if (!imageFiles.isNullOrEmpty()) {
            binding.layoutAttachmentsURLs.removeAllViews()
            // Iterasi melalui setiap URL gambar dan membuat TextView baru untuk setiap URL
            imageFiles.forEach { image ->
                val textView = TextView(this)
                textView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textView.text = "- ${image?.filename}"
                textView.setOnClickListener {
                    val imageView = ImageView(this)
                    val layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                    imageView.layoutParams = layoutParams

                    // Load image into ImageView using Glide
                    Glide.with(this)
                        .load(image?.url)
                        .placeholder(R.drawable.rounded_tag_blue700) // any placeholder to load at start
                        .error(R.drawable.rounded_tag_red700)  // any image in case of error
                        .override(1000, 1000) // resizing
                        .into(imageView)

                    // Buat AlertDialog dengan ImageView sebagai kontennya
                    val dialog = AlertDialog.Builder(this)
                        .setView(imageView)
                        .create()

                    // Tampilkan dialog
                    dialog.show()
                }
                // Tambahkan TextView ke dalam LinearLayout menggunakan ViewBinding
                binding.layoutAttachmentsURLs.addView(textView)
            }
        }

        binding.radioButton1.isChecked = requestItem?.receiveVerifyCompleted == true
        binding.radioButton2.isChecked = requestItem?.coordinateActionCompleted == true
        binding.radioButton3.isChecked = requestItem?.followUpResolveCompleted == true

        val radioButtons = listOf<RadioButton>(binding.radioButton1, binding.radioButton2, binding.radioButton3)
        for ((step, radioButton) in radioButtons.withIndex()) {
            radioButton.setOnClickListener {
                updateRequestStep(requestItem?.id!!, step+1)
            }
        }
    }
}