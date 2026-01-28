// utils/Share.kt
package com.example.tripshare

import android.content.Context
import android.content.Intent

fun shareInvite(context: Context, text: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(send, "Invite"))
}
