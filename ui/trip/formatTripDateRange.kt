package com.example.tripshare.ui.trip

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val RANGE_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

fun formatTripDateRange(start: LocalDate?, end: LocalDate?): String {
    return when {
        start != null && end != null -> "${start.format(RANGE_FMT)} – ${end.format(RANGE_FMT)}"
        start != null -> start.format(RANGE_FMT)
        end != null -> end.format(RANGE_FMT)
        else -> "Dates TBA"
    }
}
