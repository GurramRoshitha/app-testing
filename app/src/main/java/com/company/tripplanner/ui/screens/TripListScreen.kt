package com.company.tripplanner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.components.SectionHeader
import com.company.tripplanner.ui.components.StatusBadge
import com.company.tripplanner.ui.theme.MyApplicationTheme
import com.company.tripplanner.data.models.Trip
import com.company.tripplanner.data.models.getEffectiveStatus

@Composable
fun TripListScreen(
    trips: List<Trip>, 
    onTripClick: (Int) -> Unit,
    onDeleteTrip: (String) -> Unit
) {
    var tripToDelete by remember { mutableStateOf<Trip?>(null) }

    if (tripToDelete != null) {
        AlertDialog(
            onDismissRequest = { tripToDelete = null },
            title = { Text("Delete Trip") },
            text = { Text("Are you sure you want to delete '${tripToDelete?.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = tripToDelete?.id
                        if (id != null) {
                            onDeleteTrip(id)
                        } else {
                            onDeleteTrip("") 
                        }
                        tripToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { tripToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    val indexedTrips = remember(trips) { trips.mapIndexed { index, trip -> index to trip } }
    val activeTrips = remember(indexedTrips) { indexedTrips.filter { it.second.getEffectiveStatus() == "Active" } }
    val upcomingTrips = remember(indexedTrips) { indexedTrips.filter { it.second.getEffectiveStatus() == "Upcoming" } }
    val pastTrips = remember(indexedTrips) { indexedTrips.filter { it.second.getEffectiveStatus() == "Completed" } }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(text = "My Trips", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (trips.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No trips added yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // --- ACTIVE / ONGOING TRIPS SECTION ---
                    if (activeTrips.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Ongoing Trips")
                        }
                        items(activeTrips, key = { it.second.id ?: it.first.toString() }) { (realIndex, trip) ->
                            TripCard(
                                trip = trip, 
                                onClick = { onTripClick(realIndex) },
                                onDeleteClick = { tripToDelete = trip }
                            )
                        }
                    }

                    // --- UPCOMING TRIPS SECTION ---
                    item {
                        SectionHeader(title = "Upcoming Trips")
                    }
                    if (upcomingTrips.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = "No upcoming trips at the moment.", 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(upcomingTrips, key = { it.second.id ?: it.first.toString() }) { (realIndex, trip) ->
                            TripCard(
                                trip = trip, 
                                onClick = { onTripClick(realIndex) },
                                onDeleteClick = { tripToDelete = trip }
                            )
                        }
                    }

                    // --- PAST TRIPS SECTION ---
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        SectionHeader(title = "Past Trips")
                    }
                    if (pastTrips.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = "No past trips yet. Your completed adventures will appear here!", 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(pastTrips, key = { it.second.id ?: it.first.toString() }) { (realIndex, trip) ->
                            TripCard(
                                trip = trip, 
                                onClick = { onTripClick(realIndex) },
                                onDeleteClick = { tripToDelete = trip }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripCard(trip: Trip, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    val currencySymbols = mapOf(
        "USD" to "$", "EUR" to "€", "GBP" to "£", "INR" to "₹", 
        "JPY" to "¥", "AUD" to "A$", "CAD" to "C$", "SGD" to "S$",
        "AED" to "د.إ", "CNY" to "¥", "CHF" to "Fr", "KRW" to "₩",
        "NZD" to "NZ$", "MXN" to "$", "HKD" to "HK$", "THB" to "฿"
    )
    val symbol = currencySymbols[trip.currency] ?: trip.currency
    val status = trip.getEffectiveStatus()
    val badgeColor = when (status) {
        "Completed" -> MaterialTheme.colorScheme.secondary
        "Active" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    AppCard(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = trip.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = trip.dates, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${trip.members} Members", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    if (trip.total_budget > 0) {
                        Text(text = " • $symbol${trip.total_budget.toInt()}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(
                    text = status,
                    color = badgeColor
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete, 
                        contentDescription = "Delete Trip",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TripListScreenPreview() {
    MyApplicationTheme {
        TripListScreen(
            trips = listOf(Trip(name = "Tokyo Adventure", dates = "May 10 - May 20, 2026", status = "Upcoming")),
            onTripClick = {},
            onDeleteTrip = {}
        )
    }
}
