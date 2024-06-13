package com.bukitvista.gros.ui

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bukitvista.gros.R
import com.bukitvista.gros.data.ImageItem
import com.bukitvista.gros.data.RequestItem
import com.bukitvista.gros.databinding.ActivityDetailRequestBinding
import com.bukitvista.gros.response.ImageURLsItem
import com.bukitvista.gros.response.RequestsResponseItem
import com.bumptech.glide.Glide

class DetailRequestActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailRequestBinding
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

        // Mendapatkan item dari intent
        val requestItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("REQUEST_ITEM", RequestsResponseItem::class.java)
        } else {
            intent.getParcelableExtra<RequestsResponseItem>("REQUEST_ITEM")
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        setRequestData(requestItem)
    }

    private fun setRequestData(requestItem: RequestsResponseItem?) {
        binding.tvSummary.text = requestItem?.description
        binding.tvPriority.text = requestItem?.priority.toString()
        binding.tvGuestName.text = requestItem?.guestName
        binding.tvTimestamp.text = requestItem?.createdAt
        binding.tvActions.text = requestItem?.actions
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

        refreshRadioButton(requestItem)

        val radioButtons = listOf(
            binding.radioButton1,
            binding.radioButton2,
            binding.radioButton3
        )

//        for ((index, radioButton) in radioButtons.withIndex()) {
//            radioButton.setOnClickListener {
//                when (index) {
//                    0 -> {
//                        if (requestItem?.receiveVerifyCompleted == true) {
//                            requestItem.receiveVerifyCompleted = false
//                        } else {
//                            requestItem?.receiveVerifyCompleted = true
//                        }
//                    }
//                    1 -> {
//                        if (requestItem?.coordinateActionCompleted == true) {
//                            requestItem.coordinateActionCompleted = false
//                        } else {
//                            requestItem?.coordinateActionCompleted = true
//                        }
//                    }
//                    2 -> {
//                        if (requestItem?.followUpResolveCompleted == true) {
//                            requestItem.followUpResolveCompleted = false
//                        } else {
//                            requestItem?.followUpResolveCompleted = true
//                        }
//                    }
//                }
//                refreshRadioButton(requestItem)
//            }
//        }

    }

    private fun refreshRadioButton(requestItem: RequestsResponseItem?) {
        binding.radioButton1.isChecked = requestItem?.receiveVerifyCompleted == true
        binding.radioButton2.isChecked = requestItem?.coordinateActionCompleted == true
        binding.radioButton3.isChecked = requestItem?.followUpResolveCompleted == true
    }
}