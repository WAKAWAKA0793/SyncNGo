package com.example.tripshare.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SharedChecklist(
    val tripName: String,
    val categories: List<SharedCategory>
)

@Serializable
data class SharedCategory(
    val title: String,
    val items: List<SharedItem>
)

@Serializable
data class SharedItem(
    val title: String,
    val quantity: Int = 1,
    val note: String? = null
)