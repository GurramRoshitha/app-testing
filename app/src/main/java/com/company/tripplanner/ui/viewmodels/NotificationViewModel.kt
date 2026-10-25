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
import com.company.tripplanner.ui.screens.calculateDaysRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

    fun syncNotificationsWithTrips(trips: List<Trip>) {
        viewModelScope.launch(Dispatchers.Default) {
            val existingIds = _notifications.map { it.id }.toSet()
            val newItems = mutableListOf<NotificationItem>()

            trips.forEachIndexed { realIndex, trip ->
                val status = trip.getEffectiveStatus()
                val days = calculateDaysRemaining(trip.dates)

                // 1. Departure Reminders (Today or Tomorrow)
                if (days == 0L) {
                    val id = "dept_today_${trip.id ?: trip.name}"
                    if (!existingIds.contains(id)) {
                        newItems.add(
                            NotificationItem(
                                id = id,
                                tripId = trip.id,
                                tripIndex = realIndex,
                                title = "Departure Today!",
                                message = "Your trip to ${trip.name} starts today! Have a safe and wonderful journey.",
                                type = "DEPARTURE",
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                } else if (days == 1L) {
                    val id = "dept_tomorrow_${trip.id ?: trip.name}"
                    if (!existingIds.contains(id)) {
                        newItems.add(
                            NotificationItem(
                                id = id,
                                tripId = trip.id,
                                tripIndex = realIndex,
                                title = "Departure Tomorrow!",
                                message = "Get ready! Your journey to ${trip.name} starts tomorrow.",
                                type = "DEPARTURE",
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                } else if (days != null && days in 2L..7L) {
                    val id = "upcoming_7d_${trip.id ?: trip.name}"
                    if (!existingIds.contains(id)) {
                        newItems.add(
                            NotificationItem(
                                id = id,
                                tripId = trip.id,
                                tripIndex = realIndex,
                                title = "Upcoming Journey",
                                message = "Your journey to ${trip.name} begins in $days days.",
                                type = "UPCOMING",
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }

                // 2. Status Updates (Completed Trip)
                if (status == "Completed") {
                    val id = "status_completed_${trip.id ?: trip.name}"
                    if (!existingIds.contains(id)) {
                        newItems.add(
                            NotificationItem(
                                id = id,
                                tripId = trip.id,
                                tripIndex = realIndex,
                                title = "Trip Completed",
                                message = "Hope you enjoyed your journey to ${trip.name}! Check your past trips anytime.",
                                type = "STATUS",
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }

                // 3. Activity Reminders from Itinerary
                trip.itinerary.forEach { (day, activities) ->
                    activities.forEachIndexed { actIdx, act ->
                        if (act.title.isNotBlank()) {
                            val id = "act_${trip.id ?: trip.name}_${day}_${actIdx}"
                            if (!existingIds.contains(id)) {
                                newItems.add(
                                    NotificationItem(
                                        id = id,
                                        tripId = trip.id,
                                        tripIndex = realIndex,
                                        title = "Activity Reminder",
                                        message = "${act.title} (${act.time}) in ${trip.name}",
                                        type = "ACTIVITY",
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                            }
                        }
                    }
                }
            }

            if (newItems.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.Main) {
                    _notifications.addAll(0, newItems)
                    _notifications.sortByDescending { it.timestamp }
                    saveToDisk()

                    // Send Android system notification for primary departure/status alerts
                    newItems.firstOrNull { it.type == "DEPARTURE" || it.type == "UPCOMING" || it.type == "STATUS" }?.let { item ->
                        sendSystemNotification(item)
                    }
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
