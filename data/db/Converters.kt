// data/db/Converters.kt
package com.example.tripshare.data.db

import androidx.room.TypeConverter
import com.example.tripshare.data.model.MessageStatus
import com.example.tripshare.data.model.PaymentStatus
import com.example.tripshare.data.model.TripCategory
import com.example.tripshare.data.model.VerificationMethod
import com.example.tripshare.data.model.Visibility
import java.time.LocalDate

class Converters {
    @TypeConverter fun fromEpochDay(v: Long?): LocalDate? = v?.let(LocalDate::ofEpochDay)
    @TypeConverter fun toEpochDay(d: LocalDate?): Long? = d?.toEpochDay()

    @TypeConverter fun catFrom(s: String?): TripCategory? = s?.let { TripCategory.valueOf(it) }
    @TypeConverter fun catTo(c: TripCategory?): String? = c?.name

    @TypeConverter fun visFrom(s: String?): Visibility? = s?.let { Visibility.valueOf(it) }
    @TypeConverter fun visTo(v: Visibility?): String? = v?.name

    @TypeConverter
    fun toVerification(value: String): VerificationMethod =
        VerificationMethod.valueOf(value)

    @TypeConverter
    fun fromVerification(v: VerificationMethod): String = v.name

    @TypeConverter
    fun statusToString(status: MessageStatus?): String? = status?.name

    @TypeConverter
    fun stringToStatus(value: String?): MessageStatus? =
        value?.let { MessageStatus.valueOf(it) }

    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus): String {
        return status.name // Saves as "PAID", "PENDING", "PAY_NOW"
    }

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus {
        return try {
            // Tries to find matching Enum.
            // Handles case-sensitivity automatically for .name
            PaymentStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            // Fallback to prevent crash if DB has old weird data like "Pay Now" (with space)
            // You can map old strings here manually if needed:
            if (value.equals("Pay Now", ignoreCase = true)) return PaymentStatus.PAY_NOW

            PaymentStatus.PENDING // Default fallback
        }
    }
}
