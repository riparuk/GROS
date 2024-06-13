package com.bukitvista.gros.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class StaffResponseNotFound(

	@field:SerializedName("detail")
	val detail: String? = null
) : Parcelable
