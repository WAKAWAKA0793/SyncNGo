package com.example.tripshare.data.model

// data/model/TripDetailsUiModel.kt


data class TripDetailsUiModel(
    val trip: TripEntity,
    val participants: List<TripParticipantEntity> = emptyList(),
    val meetingPoints: List<TripMeetingPointEntity> = emptyList(),
    val itinerary: List<ItineraryItemEntity> = emptyList(),
    val organizerRating: OrganizerRating? = null
)

data class OrganizerRating(
    val organizerName: String,
    val avatarUrl: String? = null,
    val averageRating: Double,
    val reviewCount: Int
)
