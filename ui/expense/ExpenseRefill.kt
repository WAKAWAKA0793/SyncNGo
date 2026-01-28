package com.example.tripshare.ui.expense

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExpensePrefill(
    val description: String,
    val dateIso: String,     // store as String (safe for Parcelable)
    val category: String
) : Parcelable
