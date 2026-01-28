// ui/home/TripUi.kt
package com.example.tripshare.ui.home

import com.example.tripshare.data.model.RouteStopEntity
import com.example.tripshare.data.model.StopType
import com.example.tripshare.data.model.TripEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class TripStatus { UPCOMING, PAST }

data class TripUi(
    val id: Long,
    val title: String,
    val location: String,
    val dateRange: String,

    // 🔹 UPDATED: Separated current count vs limit
    val travelers: Int,      // Current number of people joined
    val maxTravelers: Int,   // Maximum capacity (from Entity)

    val imageUrl: String,
    val status: TripStatus,
    val description: String = "",
    val startDateRaw: String? = null,
    val endDateRaw: String? = null,
    val startDateIso: String?,
    val endDateIso: String?,
    val budgetDisplay: String? = null,
    val category: String = "",
    val startLat: Double? = null,
    val startLng: Double? = null,
    val joined: Boolean = false
)

private val rangeFmt: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

private fun LocalDate?.fmt(): String? = this?.format(rangeFmt)

private fun String.titleCaseEnum(): String =
    lowercase(Locale.getDefault())
        .replace('_', ' ')
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

fun primaryLocationFromStops(stops: List<RouteStopEntity>?): String? {
    if (stops.isNullOrEmpty()) return null
    val start = stops.firstOrNull { it.type == StopType.START && it.label.isNotBlank() }
    if (start != null) return start.label
    return stops.firstOrNull { it.label.isNotBlank() }?.label
}

// 🔹 UPDATED: Added participantCount so we know how many people are currently in the trip
fun TripEntity.toUi(
    status: TripStatus,
    joined: Boolean = false,
    stops: List<RouteStopEntity>? = null,
    participantCount: Int = 1
): TripUi {

    val startStop = stops?.firstOrNull { it.type == StopType.START }
    val derivedLocation = startStop?.label?.takeIf { it.isNotBlank() }
        ?: primaryLocationFromStops(stops)
        ?: category.name.titleCaseEnum()

    val startLat = startStop?.lat
    val startLng = startStop?.lng

    val displayImage = coverImgUrl ?: "https://picsum.photos/seed/$id/800/500"

    return TripUi(
        id = id,
        title = name,
        location = derivedLocation,
        dateRange = when {
            startDate != null && endDate != null -> "${startDate.fmt()} – ${endDate.fmt()}"
            startDate != null -> "From ${startDate.fmt()}"
            endDate != null -> "Until ${endDate.fmt()}"
            else -> "Anytime"
        },
        travelers = participantCount,
        maxTravelers = maxParticipants,

        imageUrl = displayImage,

        status = status,
        description = description,
        startDateRaw = startDate?.toString(),
        endDateRaw = endDate?.toString(),
        startDateIso = startDate?.toString(),
        endDateIso   = endDate?.toString(),

        // ✅ THIS LINE FIXES YOUR ISSUE
        budgetDisplay = budgetDisplay,

        category = category.name.titleCaseEnum(),
        startLat = startLat,
        startLng = startLng,
        joined = joined
    )
}
