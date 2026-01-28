package com.example.tripshare

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class TripShare : Application() {
    override fun onCreate() {
        super.onCreate()
        // ✅ Initialize ThreeTenABP so LocalDate works on API < 26
        AndroidThreeTen.init(this)

    }
}
