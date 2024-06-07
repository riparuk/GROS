package com.bukitvista.gros.data

data class ChecklistItem(
    val point: String,
)

data class Checklist(
    val title: String,
    val items: List<ChecklistItem>,
    val completed: Boolean = false
)

data class RequestItem(
    val timestamp: String,
    val guestName: String,
    val description: String,
    val actions: String? = null,
    val priority: String,
    val progress: String,
    val staffName: String? = null,
    val staffImageURL: String? = null,
    val checklists: List<Checklist>? = null,
    val imageURLs: List<String>? = null,
    val notes: String? = null,
)
