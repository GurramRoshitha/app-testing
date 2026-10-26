package com.company.tripplanner.ui.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.company.tripplanner.data.models.Trip
import com.company.tripplanner.data.models.VoyageActivity
import com.company.tripplanner.data.services.TripService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class TripViewModel(application: Application) : AndroidViewModel(application) {
    private val tripService by lazy { TripService() }
    private val prefs = application.getSharedPreferences("voyage_disk_cache", Context.MODE_PRIVATE)
    private val jsonFormatter = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    private val _trips = mutableStateListOf<Trip>()
    val trips: List<Trip> = _trips

    private val _isGenerating = mutableStateOf(false)
    val isGenerating: androidx.compose.runtime.State<Boolean> = _isGenerating

    private val stateHubs = mapOf(
        "andhra pradesh" to listOf("Tirupati", "Visakhapatnam", "Araku Valley", "Vijayawada", "Srisailam", "Gandikota", "Amaravati", "Rajahmundry", "Kurnool", "Horsley Hills"),
        "arunachal pradesh" to listOf("Tawang", "Itanagar", "Ziro", "Bomdila", "Dirang", "Namdapha", "Miao"),
        "assam" to listOf("Guwahati", "Kaziranga", "Majuli", "Sivasagar", "Manas", "Haflong"),
        "bihar" to listOf("Patna", "Bodh Gaya", "Nalanda", "Rajgir", "Vaishali", "Pawapuri"),
        "chhattisgarh" to listOf("Raipur", "Jagdalpur", "Dantewada", "Sirpur", "Mainpat"),
        "goa" to listOf("Panaji", "North Goa", "South Goa", "Dudhsagar Falls", "Old Goa"),
        "gujarat" to listOf("Ahmedabad", "Statue of Unity", "Dwarka", "Somnath", "Kutch", "Gir"),
        "haryana" to listOf("Gurugram", "Kurukshetra", "Panchkula", "Panipat", "Faridabad"),
        "himachal pradesh" to listOf("Shimla", "Manali", "Dharamshala", "Dalhousie", "Spiti Valley", "Kasol"),
        "jharkhand" to listOf("Ranchi", "Deoghar", "Netarhat", "Jamshedpur", "Hazaribagh"),
        "karnataka" to listOf("Bengaluru", "Mysuru", "Hampi", "Coorg", "Chikkamagaluru", "Gokarna", "Udupi"),
        "kerala" to listOf("Kochi", "Munnar", "Alleppey", "Thekkady", "Wayanad", "Kovalam", "Varkala"),
        "madhya pradesh" to listOf("Bhopal", "Indore", "Khajuraho", "Ujjain", "Gwalior", "Pachmarhi", "Jabalpur"),
        "maharashtra" to listOf("Mumbai", "Pune", "Mahabaleshwar", "Lonavala", "Nashik", "Aurangabad", "Shirdi"),
        "manipur" to listOf("Imphal", "Loktak Lake", "Moirang", "Ukhrul"),
        "meghalaya" to listOf("Shillong", "Cherrapunji", "Mawlynnong", "Nongriat", "Dawki", "Jowai"),
        "mizoram" to listOf("Aizawl", "Reiek", "Hmuifang", "Thenzawl", "Champhai"),
        "nagaland" to listOf("Kohima", "Dzukou Valley", "Mokokchung", "Mon"),
        "odisha" to listOf("Bhubaneswar", "Puri", "Konark", "Chilika", "Cuttack", "Simlipal"),
        "punjab" to listOf("Amritsar", "Chandigarh", "Ludhiana", "Patiala", "Anandpur Sahib"),
        "rajasthan" to listOf("Jaipur", "Udaipur", "Jodhpur", "Jaisalmer", "Pushkar", "Ranthambore", "Mount Abu"),
        "sikkim" to listOf("Gangtok", "Pelling", "Lachung", "Lachen", "Namchi"),
        "tamil nadu" to listOf("Chennai", "Ooty", "Kodaikanal", "Madurai", "Rameswaram", "Thanjavur", "Mahabalipuram", "Kanyakumari"),
        "telangana" to listOf("Hyderabad", "Warangal", "Nizamabad", "Adilabad", "Nagarjuna Sagar"),
        "tripura" to listOf("Agartala", "Unakoti", "Neermahal", "Jampui Hills", "Sepahijala"),
        "uttar pradesh" to listOf("Agra", "Varanasi", "Lucknow", "Ayodhya", "Mathura", "Vrindavan", "Prayagraj"),
        "uttarakhand" to listOf("Dehradun", "Mussoorie", "Rishikesh", "Haridwar", "Nainital", "Kedarnath", "Badrinath", "Valley of Flowers"),
        "west bengal" to listOf("Kolkata", "Darjeeling", "Kalimpong", "Sundarbans", "Digha", "Bishnupur"),
        "andaman and nicobar islands" to listOf("Port Blair", "Havelock Island", "Neil Island", "Baratang Island"),
        "chandigarh" to listOf("Chandigarh City"),
        "dadra and nagar haveli and daman and diu" to listOf("Daman", "Diu", "Silvassa"),
        "delhi" to listOf("New Delhi", "Old Delhi"),
        "jammu and kashmir" to listOf("Srinagar", "Gulmarg", "Pahalgam", "Sonamarg", "Jammu"),
        "ladakh" to listOf("Leh", "Nubra Valley", "Pangong Lake", "Tso Moriri", "Sham Valley"),
        "lakshadweep" to listOf("Kavaratti", "Agatti", "Bangaram", "Kalpeni", "Minicoy"),
        "puducherry" to listOf("Puducherry City", "Auroville", "Paradise Beach")
    )

    private val districtPlaces = mapOf(
        "tirupati" to listOf("Tirumala Venkateswara Temple", "Sri Govindaraja Swamy Temple", "Kapila Theertham", "Sri Padmavathi Ammavari Temple", "ISKCON Temple"),
        "visakhapatnam" to listOf("RK Beach", "Rushikonda Beach", "Yarada Beach", "Kailasagiri", "INS Kurusura Submarine Museum", "Simhachalam Temple", "Thotlakonda Buddhist Complex"),
        "araku valley" to listOf("Tribal Museum", "Coffee Museum", "Padmapuram Gardens", "Chaparai Waterfalls", "Galikonda View Point"),
        "vijayawada" to listOf("Kanaka Durga Temple", "Prakasam Barrage", "Undavalli Caves", "Bhavani Island", "Gandhi Hill", "Mogalarajapuram Caves"),
        "srisailam" to listOf("Mallikarjuna Swamy Temple", "Bhramaramba Devi Temple", "Srisailam Dam", "Pathala Ganga", "Akka Mahadevi Caves", "Srisailam Tiger Reserve"),
        "gandikota" to listOf("Gandikota Fort", "Gandikota Canyon", "Madhavaraya Temple", "Ranganatha Swamy Temple", "Jamia Masjid"),
        "amaravati" to listOf("Amaravati Stupa", "Amaralingeswara Temple", "Dhyana Buddha Statue"),
        "rajahmundry" to listOf("Godavari River", "Dowleswaram Barrage", "ISKCON Temple", "Papi Hills"),
        "kurnool" to listOf("Belum Caves", "Oravakallu Rock Garden", "Kondareddy Buruju", "Yaganti Temple"),
        "horsley hills" to listOf("Horsley Hills Viewpoint", "Mallamma Temple", "Gangotri Lake"),
        "kochi" to listOf("Fort Kochi", "Chinese Fishing Nets", "Mattancherry Palace", "Jewish Synagogue", "St. Francis Church"),
        "munnar" to listOf("Eravikulam National Park", "Mattupetty Dam", "Echo Point", "Tea Gardens", "Top Station"),
        "alleppey" to listOf("Alleppey Backwaters", "Houseboat cruises", "Alappuzha Beach", "Vembanad Lake")
    )

    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    init {
        loadTrips()
        startListeningToTrips()
    }

    fun startListeningToTrips() {
        listenerRegistration?.remove()
        listenerRegistration = tripService.observeTrips { remoteTrips ->
            viewModelScope.launch(Dispatchers.Main) {
                _trips.clear()
                _trips.addAll(remoteTrips)
                saveTripsToDisk()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    private fun saveTripsToDisk() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = _trips.toList()
                val jsonString = jsonFormatter.encodeToString(list)
                prefs.edit().putString("saved_trips_json", jsonString).apply()
                Log.d("TripViewModel", "Permanently saved ${list.size} trips to disk")
            } catch (e: Exception) {
                Log.e("TripViewModel", "Failed saving trips to disk", e)
            }
        }
    }

    private fun loadTripsFromDisk(): List<Trip> {
        return try {
            val jsonString = prefs.getString("saved_trips_json", null)
            if (!jsonString.isNullOrEmpty()) {
                jsonFormatter.decodeFromString<List<Trip>>(jsonString)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("TripViewModel", "Failed loading trips from disk", e)
            emptyList()
        }
    }

    fun loadTrips() {
        viewModelScope.launch {
            // 1. Instantly restore trips from disk cache so data NEVER vanishes
            val diskTrips = withContext(Dispatchers.IO) { loadTripsFromDisk() }
            if (diskTrips.isNotEmpty()) {
                _trips.clear()
                _trips.addAll(diskTrips)
            }

            // 2. Sync with remote server if available
            try {
                val result = tripService.getTrips()
                val remoteTrips = result.getOrDefault(emptyList())
                if (remoteTrips.isNotEmpty()) {
                    val combined = (remoteTrips + diskTrips)
                        .distinctBy { it.id ?: "${it.name}_${it.dates}" }
                        .sortedByDescending { it.createdAt }

                    _trips.clear()
                    _trips.addAll(combined)
                    saveTripsToDisk()
                }
            } catch (e: Exception) {
                Log.e("TripViewModel", "Remote load failed, using disk cache", e)
            }
        }
    }

    fun addTrip(
        destination: String, 
        startDate: String, 
        endDate: String, 
        members: Int, 
        budget: Double = 0.0,
        currency: String = "USD",
        onSuccess: (Int) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _isGenerating.value = true
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val start = try { dateFormat.parse(startDate) } catch (e: Exception) { Date() }
                val end = try { dateFormat.parse(endDate) } catch (e: Exception) { Date() }
                val diff = ((end?.time ?: 0) - (start?.time ?: 0)) / (1000 * 60 * 60 * 24)
                val dayCount = Math.max(1, diff.toInt() + 1)

                val destinationLower = destination.lowercase().trim()
                val hubs = stateHubs[destinationLower] ?: listOf(destination)

                val currencySymbols = mapOf("USD" to "$", "EUR" to "€", "GBP" to "£", "INR" to "₹")
                val symbol = currencySymbols[currency] ?: "$"

                val generatedItinerary = mutableMapOf<String, List<VoyageActivity>>()
                val dailyBudget = if (dayCount > 0) budget / dayCount else 0.0
                var totalAllocated = 0.0

                val daysPerHub = (dayCount.toDouble() / hubs.size).coerceAtLeast(1.0)

                for (day in 1..dayCount) {
                    val dayActivities = mutableListOf<VoyageActivity>()
                    val currentHubIndex = ((day - 1) / daysPerHub).toInt().coerceAtMost(hubs.size - 1)
                    val currentHub = hubs[currentHubIndex]
                    val isFirstDayInHub = (day == 1) || (((day - 1) % daysPerHub.toInt()) == 0 && currentHubIndex > 0)

                    val morningSlotTime = "08:00 AM"
                    if (isFirstDayInHub) {
                        val title = if (day == 1) "Check-in at Premium Hotel in $currentHub" else "Checkout & Travel to $currentHub"
                        val desc = if (day == 1) "Starting your journey in $currentHub. Settle into your base." 
                                   else "Moving to the next district. Enjoy a scenic morning transit to $currentHub."
                        
                        dayActivities.add(VoyageActivity(morningSlotTime, title, "Accommodation", "", desc, "Stay"))
                    } else {
                        dayActivities.add(VoyageActivity(morningSlotTime, "Leisurely Morning in $currentHub", "Leisure", "", "Wake up and enjoy local vibes.", "Activity"))
                    }

                    val hubPlaces = districtPlaces[currentHub.lowercase()] ?: listOf("Local Attraction", "City Landmark", "Scenic Spot", "Cultural Site", "Historic Monument")
                    
                    val sightAct = hubPlaces[day % hubPlaces.size]
                    dayActivities.add(VoyageActivity("10:30 AM", sightAct, "Sightseeing", "", "Visit $sightAct in $currentHub.", "Activity"))

                    dayActivities.add(VoyageActivity("01:30 PM", "Authentic $currentHub Lunch", "Restaurant", "", "Taste local flavors in $currentHub.", "Restaurant"))

                    val cultAct = hubPlaces[(day + 1) % hubPlaces.size]
                    dayActivities.add(VoyageActivity("04:30 PM", cultAct, "Culture", "", "Experience $cultAct.", "Activity"))

                    dayActivities.add(VoyageActivity("07:30 PM", "$currentHub Evening Exploration", "Leisure", "", "Relaxing evening in $currentHub.", "Activity"))

                    generatedItinerary[day.toString()] = dayActivities
                }

                val dates = if (startDate.isNotEmpty() && endDate.isNotEmpty()) "$startDate - $endDate" else "TBD"
                val generatedId = UUID.randomUUID().toString()
                val newTrip = Trip(
                    id = generatedId,
                    name = destination, dates = dates, status = "Upcoming", 
                    members = members, total_budget = budget, currency = currency,
                    createdAt = System.currentTimeMillis(), itinerary = generatedItinerary
                )
                
                // Save to state list & disk
                _trips.add(0, newTrip)
                saveTripsToDisk()

                tripService.createTrip(newTrip)

                _isGenerating.value = false
                onSuccess(0)
            } catch (e: Exception) {
                _isGenerating.value = false
                Log.e("TripViewModel", "Error adding trip", e)
            }
        }
    }

    fun deleteTrip(tripId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _trips.removeAll { it.id == tripId }
            saveTripsToDisk()
            tripService.deleteTrip(tripId)
            onSuccess()
        }
    }

    fun updateTripStatus(tripId: String, newStatus: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val index = _trips.indexOfFirst { it.id == tripId }
            if (index != -1) {
                val updatedTrip = _trips[index].copy(status = newStatus)
                _trips[index] = updatedTrip
                saveTripsToDisk()
                tripService.updateTripStatus(tripId, newStatus)
            }
            onSuccess()
        }
    }
}
