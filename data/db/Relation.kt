package com.example.tripshare.data.db

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tripshare.data.model.ChecklistCategoryEntity
import com.example.tripshare.data.model.ChecklistItemEntity
import com.example.tripshare.data.model.ItineraryItemEntity
import com.example.tripshare.data.model.ParticipantInviteEntity
import com.example.tripshare.data.model.ReviewEntity
import com.example.tripshare.data.model.RouteStopEntity
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.TripMeetingPointEntity
import com.example.tripshare.data.model.TripParticipantEntity
import com.example.tripshare.data.model.TripPaymentMethodEntity
import com.example.tripshare.data.model.UserEntity

data class TripFullAggregate(
    @Embedded val trip: TripEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val routeStops: List<RouteStopEntity> = emptyList(),

    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val participants: List<TripParticipantEntity> = emptyList(),

    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val meetingPoints: List<TripMeetingPointEntity> = emptyList(),

    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val itinerary: List<ItineraryItemEntity> = emptyList(),

    // Payment methods
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val paymentMethods: List<TripPaymentMethodEntity>,

    // Invites
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val invites: List<ParticipantInviteEntity>,

)

data class CategoryWithItems(
    @Embedded val category: ChecklistCategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val items: List<ChecklistItemEntity>
)

data class ReviewWithReviewer(
    @Embedded val review: ReviewEntity,

    @Relation(
        parentColumn = "reviewerId",
        entityColumn = "id"
    )
    val reviewer: UserEntity? // Nullable in case user is deleted
)