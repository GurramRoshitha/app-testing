package com.company.tripplanner.data.services

import android.util.Log
import com.company.tripplanner.data.models.Trip
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class TripService {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val tripsCollection = firestore.collection("trips")

    suspend fun createTrip(trip: Trip): Result<String> {
        return withContext(Dispatchers.IO) {
            val generatedId = UUID.randomUUID().toString()
            val userId = auth.currentUser?.uid ?: "guest_user_123"
            val tripId = trip.id.takeIf { !it.isNullOrEmpty() } ?: generatedId
            val tripData = trip.copy(id = tripId, user_id = userId)

            try {
                tripsCollection.document(tripId).set(tripData).await()
            } catch (e: Exception) {
                Log.w("TripService", "Firestore write skipped/offline: ${e.message}")
            }
            Result.success(tripId)
        }
    }

    suspend fun getTrips(): Result<List<Trip>> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid ?: "guest_user_123"
                val snapshot = tripsCollection
                    .whereEqualTo("user_id", userId)
                    .get()
                    .await()
                val trips = snapshot.documents.mapNotNull { doc ->
                    val trip = doc.toObject(Trip::class.java)
                    trip?.copy(id = doc.id)
                }
                Result.success(trips)
            } catch (e: Exception) {
                Log.w("TripService", "Firestore fetch error: ${e.message}")
                Result.success(emptyList())
            }
        }
    }

    suspend fun deleteTrip(tripId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (tripId.isNotEmpty()) {
                    tripsCollection.document(tripId).delete().await()
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Log.w("TripService", "Firestore delete failed: ${e.message}")
                Result.success(Unit)
            }
        }
    }

    suspend fun updateTripStatus(tripId: String, newStatus: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (tripId.isNotEmpty()) {
                    tripsCollection.document(tripId).update("status", newStatus).await()
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Log.w("TripService", "Firestore status update failed: ${e.message}")
                Result.success(Unit)
            }
        }
    }
}
