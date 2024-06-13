package com.bukitvista.gros.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestItem(
    val id: String,
    val timestamp: String,
    val guestId: String,
    val guestName: String,
    val description: String,
    val actions: String? = null,
    val priority: Double,
    val staffId: String? = null,
    val staffName: String? = null,
    val staffImageURL: String? = null,
    val imageFiles: List<ImageItem>? = null,
    val notes: String? = null,
    val progress: String,
    var receiveVerifyCompleted: Boolean,
    var coordinateActionCompleted: Boolean,
    var followUpResolveCompleted: Boolean,
): Parcelable

@Parcelize
data class ImageItem(
    val name: String,
    val url: String
): Parcelable
