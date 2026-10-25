package com.company.tripplanner.data.models

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class VoyageActivity(
    val time: String = "",
    val title: String = "",
    val category: String = "",
    val cost: String = "",
    val description: String = "",
    val type: String = "Activity" // Activity, Hotel, Restaurant
)

@Serializable
data class Trip(
    val id: String? = null,
    val user_id: String? = null,
    val name: String = "",
    val dates: String = "",
    val status: String = "Upcoming",
    val members: Int = 1,
    val total_budget: Double = 0.0,
    val currency: String = "USD",
    val travel_pace: String = "Balanced",
    val interests: String = "",
    val createdAt: Long = 0L,
    val itinerary: Map<String, List<VoyageActivity>> = emptyMap()
)

@Serializable
data class NotificationItem(
    val id: String = "",
    val tripId: String? = null,
    val tripIndex: Int = -1,
    val title: String = "",
    val message: String = "",
    val type: String = "INFO", // UPCOMING, DEPARTURE, ACTIVITY, STATUS, BUDGET
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Serializable
data class PackingItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val category: String = "Travel Essentials",
    val quantity: String = "1",
    val isPacked: Boolean = false,
    val isCustom: Boolean = false
)

fun Trip.getEffectiveStatus(): String {
    if (this.dates.isBlank()) {
        return if (this.status.equals("Completed", ignoreCase = true)) "Completed" else "Upcoming"
    }

    val parts = this.dates.split("-")
    val startStr = parts.firstOrNull()?.trim() ?: ""
    val endStr = parts.getOrNull(1)?.trim() ?: startStr

    val formats = arrayOf(
        SimpleDateFormat("MMM dd, yyyy", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.US),
        SimpleDateFormat("dd/MM/yyyy", Locale.US),
        SimpleDateFormat("MM/dd/yyyy", Locale.US),
        SimpleDateFormat("MMM d, yyyy", Locale.US)
    )

    var startDate: Date? = null
    var endDate: Date? = null

    for (fmt in formats) {
        if (startDate == null && startStr.isNotEmpty()) {
            try { startDate = fmt.parse(startStr) } catch (e: Exception) {}
        }
        if (endDate == null && endStr.isNotEmpty()) {
            try { endDate = fmt.parse(endStr) } catch (e: Exception) {}
        }
    }

    if (startDate == null) {
        return if (this.status.equals("Completed", ignoreCase = true)) "Completed" else "Upcoming"
    }

    val finalEndDate = endDate ?: startDate

    val todayCal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val startCal = Calendar.getInstance().apply {
        time = startDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val endCal = Calendar.getInstance().apply {
        time = finalEndDate
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }

    return when {
        todayCal.timeInMillis > endCal.timeInMillis -> "Completed"
        todayCal.timeInMillis >= startCal.timeInMillis -> "Active"
        this.status.equals("Completed", ignoreCase = true) -> "Completed"
        else -> "Upcoming"
    }
}

data class UserProfile(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val profilePictureUri: String = "",
    val isRegistered: Boolean = false
)
