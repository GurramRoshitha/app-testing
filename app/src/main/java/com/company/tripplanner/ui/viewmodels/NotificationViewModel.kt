package com.company.tripplanner.ui.viewmodels

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.company.tripplanner.data.models.NotificationItem
import com.company.tripplanner.data.models.Trip
import com.company.tripplanner.data.models.getEffectiveStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("voyage_notification_prefs", Context.MODE_PRIVATE)
    private val jsonFormatter = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    private val _notifications = mutableStateListOf<NotificationItem>()
    val notifications: SnapshotStateList<NotificationItem> = _notifications

    val unreadCount: Int
        get() = _notifications.count { !it.isRead }

    init {
        loadFromDisk()
    }

    private fun loadFromDisk() {
        try {
            val jsonString = prefs.getString("saved_notifications_json", null)
            if (!jsonString.isNullOrEmpty()) {
                val list = jsonFormatter.decodeFromString<List<NotificationItem>>(jsonString)
                _notifications.clear()
                _notifications.addAll(list.sortedByDescending { it.timestamp })
            }
        } catch (e: Exception) {
            Log.e("NotificationVM", "Failed loading notifications from disk", e)
        }
    }

    private fun saveToDisk() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonString = jsonFormatter.encodeToString(_notifications.toList())
                prefs.edit().putString("saved_notifications_json", jsonString).apply()
            } catch (e: Exception) {
                Log.e("NotificationVM", "Failed saving notifications to disk", e)
            }
        }
    }

    private fun getRecordedEvents(): MutableSet<String> {
        return prefs.getStringSet("recorded_events_set", emptySet())?.toMutableSet() ?: mutableSetOf()
    }

    private fun saveRecordedEvents(set: Set<String>) {
        prefs.edit().putStringSet("recorded_events_set", set).apply()
    }

    fun markAsRead(id: String) {
        val index = _notifications.indexOfFirst { it.id == id }
        if (index != -1) {
            _notifications[index] = _notifications[index].copy(isRead = true)
            saveToDisk()
        }
    }

    fun markAllAsRead() {
        for (i in _notifications.indices) {
            if (!_notifications[i].isRead) {
                _notifications[i] = _notifications[i].copy(isRead = true)
            }
        }
        saveToDisk()
    }

    fun deleteNotification(id: String) {
        _notifications.removeAll { it.id == id }
        saveToDisk()
    }

    fun clearAll() {
        _notifications.clear()
        saveToDisk()
    }

    private fun calculateDaysRemainingForTrip(datesStr: String): Long? {
        if (datesStr.isBlank()) return null
        val parts = datesStr.split("-")
        val startStr = parts.firstOrNull()?.trim() ?: ""

        val formats = arrayOf(
            SimpleDateFormat("MMM dd, yyyy", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US),
            SimpleDateFormat("dd/MM/yyyy", Locale.US),
            SimpleDateFormat("MM/dd/yyyy", Locale.US)
        )

        var startDate: Date? = null
        for (fmt in formats) {
            if (startDate == null && startStr.isNotEmpty()) {
                try { startDate = fmt.parse(startStr) } catch (e: Exception) {}
            }
        }

        if (startDate == null) return null

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

        val diffTime = startCal.timeInMillis - todayCal.timeInMillis
        return Math.ceil(diffTime.toDouble() / (1000 * 60 * 60 * 24)).toLong()
    }

    fun syncNotificationsWithTrips(trips: List<Trip>) {
        viewModelScope.launch(Dispatchers.Default) {
            val recordedEvents = getRecordedEvents()
            val newItems = mutableListOf<NotificationItem>()

            trips.forEachIndexed { realIndex, trip ->
                val tripId = trip.id ?: trip.name
                val status = trip.getEffectiveStatus()
                val days = calculateDaysRemainingForTrip(trip.dates)

                // 1. Trip Start Reminders (0 to 3 days remaining)
                if (days != null && days in 0L..3L) {
                    val eventId = "${tripId}_start_$days"
                    if (!recordedEvents.contains(eventId)) {
                        recordedEvents.add(eventId)
                        val (title, message) = when (days) {
                            0L -> "Your Trip Starts Today!" to "Your trip to ${trip.name} starts today! Have a wonderful journey!"
                            1L -> "Your Trip Starts Tomorrow!" to "Your trip to ${trip.name} starts tomorrow! Get ready for your adventure!"
                            2L -> "Your Trip Starts in 2 Days!" to "Your trip to ${trip.name} starts in 2 days!"
                            else -> "Your Trip Starts in 3 Days!" to "Your trip to ${trip.name} starts in 3 days!"
                        }

                        newItems.add(
                            NotificationItem(
                                id = eventId,
                                tripId = trip.id,
                                tripIndex = realIndex,
                                title = title,
                                message = message,
                                type = "DEPARTURE",
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }

                // 2. Trip Completion Event
                if (status == "Completed") {
                    val eventId = "${tripId}_completed"
                    if (!recordedEvents.contains(eventId)) {
                        recordedEvents.add(eventId)
                        newItems.add(
                            NotificationItem(
                                id = eventId,
                                tripId = trip.id,
                                tripIndex = realIndex,
                                title = "Your Trip is Completed!",
                                message = "Your trip to ${trip.name} is completed! We hope you had a wonderful adventure!",
                                type = "STATUS",
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }

            if (newItems.isNotEmpty()) {
                saveRecordedEvents(recordedEvents)
                viewModelScope.launch(Dispatchers.Main) {
                    _notifications.addAll(0, newItems)
                    _notifications.sortByDescending { it.timestamp }
                    saveToDisk()

                    newItems.firstOrNull()?.let { sendSystemNotification(it) }
                }
            }
        }
    }

    private fun sendSystemNotification(item: NotificationItem) {
        try {
            val context = getApplication<Application>()
            val channelId = "voyage_trip_reminders"
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Voyage Trip Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifications for upcoming trips and itinerary reminders"
                }
                notificationManager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(item.title)
                .setContentText(item.message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            notificationManager.notify(item.id.hashCode(), builder.build())
        } catch (e: Exception) {
            Log.e("NotificationVM", "System notification error", e)
        }
    }
}
