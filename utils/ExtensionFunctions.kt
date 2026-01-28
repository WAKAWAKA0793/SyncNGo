package com.example.tripshare.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 *  This function return a readable date based on the timestamp of the last message
 *  @return a String (Readable time/date)
 *  */
fun Long.toReadableString(): String {
    val currentDate = Date(this)
    val cal = Calendar.getInstance()
    cal.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // If timestamp is of today, return hh:mm format
    if (currentDate.after(cal.time)) {
        return SimpleDateFormat("kk:mm", Locale.getDefault()).format(currentDate)
    }

    cal.add(Calendar.DATE, -1)
    return if (currentDate.after(cal.time)) {
        // If timestamp is of yesterday return "Yesterday"
        "Yesterday"
    } else {
        // If timestamp is older return the date, for eg. "Jul 05"
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(currentDate)
    }
}