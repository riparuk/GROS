package com.bukitvista.gros.data

data class RequestHistory(
    val timestamp: String,
    val description: String,
    val staffName: String? = null,
)
