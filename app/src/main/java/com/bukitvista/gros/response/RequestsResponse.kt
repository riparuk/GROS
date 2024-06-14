package com.bukitvista.gros.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class RequestsResponse(

	@field:SerializedName("RequestsResponse")
	val requestsResponse: List<RequestsResponseItem>? = null
) : Parcelable

@Parcelize
data class ImageURLsItem(

	@field:SerializedName("filename")
	val filename: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("url")
	val url: String? = null
) : Parcelable

@Parcelize
data class RequestsResponseItem(

	@field:SerializedName("notes")
	val notes: String? = null,

	@field:SerializedName("request_message")
	val requestMessage: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("priority")
	val priority: Int? = null,

	@field:SerializedName("isDone")
	val isDone: Boolean? = null,

	@field:SerializedName("property_id")
	val propertyId: String? = null,

	@field:SerializedName("guestName")
	val guestName: String? = null,

	@field:SerializedName("coordinateActionCompleted")
	val coordinateActionCompleted: Boolean? = null,

	@field:SerializedName("staffImageURL")
	val staffImageURL: ImageURLsItem? = null,

	@field:SerializedName("guest_id")
	val guestId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("imageURLs")
	var imageURLs: List<ImageURLsItem?>? = null,

	@field:SerializedName("staffName")
	val staffName: String? = null,

	@field:SerializedName("receiveVerifyCompleted")
	val receiveVerifyCompleted: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("followUpResolveCompleted")
	val followUpResolveCompleted: Boolean? = null,

	@field:SerializedName("actions")
	val actions: String? = null,

	@field:SerializedName("assignTo")
	val assignTo: Int? = null
) : Parcelable
