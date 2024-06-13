package com.bukitvista.gros.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class StaffResponse(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("photo")
	val photo: Photo? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("request_handled")
	val requestHandled: Int? = null,

	@field:SerializedName("property_id")
	val propertyId: String? = null
) : Parcelable

@Parcelize
data class Photo(

	@field:SerializedName("filename")
	val filename: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("url")
	val url: String? = null
) : Parcelable
