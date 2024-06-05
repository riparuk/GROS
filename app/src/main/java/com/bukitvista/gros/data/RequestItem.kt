package com.bukitvista.gros.data

data class RequestItem(
    val timestamp: String,
    val guestName: String,
    val description: String,
    val actions: String? = null,
    val priority: String,
    val progress: String,
    val staffName: String? = null,
    val staffImageURL: String? = null
)
