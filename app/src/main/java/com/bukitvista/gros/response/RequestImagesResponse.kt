package com.bukitvista.gros.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class RequestImagesResponse(

	@field:SerializedName("RequestImagesResponse")
	val requestImagesResponse: List<RequestImagesResponseItem?>? = null
) : Parcelable

@Parcelize
data class RequestImagesResponseItem(

	@field:SerializedName("filename")
	val filename: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("url")
	val url: String? = null
) : Parcelable
