package com.bukitvista.gros.data

data class RequestItem(
    val timestamp: String,
    val guestId: String,
    val guestName: String,
    val description: String,
    val actions: String? = null,
    val priority: String,
    val staffId: String? = null,
    val staffName: String? = null,
    val staffImageURL: String? = null,
    val imageURLs: List<String>? = null,
    val notes: String? = null,
    val progress: String,
    val receiveVerifyCompleted: Boolean = false,
    val coordinateActionCompleted: Boolean = false,
    val followUpResolveCompleted: Boolean = false,
)
