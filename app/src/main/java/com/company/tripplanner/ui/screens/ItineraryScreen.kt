package com.company.tripplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import com.company.tripplanner.utils.Helpers
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.components.StatusBadge
import com.company.tripplanner.ui.theme.MyApplicationTheme
import com.company.tripplanner.data.models.Trip
import com.company.tripplanner.data.models.VoyageActivity
import com.company.tripplanner.utils.PdfExporter

@Composable
fun ItineraryScreen(tripName: String, itineraryMap: Map<String, List<VoyageActivity>>, trip: Trip? = null) {
    var selectedDay by remember { mutableStateOf("1") }
    val days = itineraryMap.keys.toList().sortedBy { it.toIntOrNull() ?: 0 }
    val context = LocalContext.current
    
    val activities = itineraryMap[selectedDay] ?: emptyList()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val currentTrip = trip ?: Trip(name = tripName, itinerary = itineraryMap)
                    PdfExporter.exportTripToPdf(context, currentTrip)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Icon(Icons.Default.PictureAsPdf, "Export PDF")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "AI Smart Planner", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Row {
                        IconButton(
                            onClick = {
                                Helpers.openDestinationInGoogleMaps(context, tripName)
                            }
                        ) {
                            Icon(Icons.Default.Place, contentDescription = "Open in Maps", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(
                            onClick = {
                                val currentTrip = trip ?: Trip(name = tripName, itinerary = itineraryMap)
                                PdfExporter.exportTripToPdf(context, currentTrip)
                            }
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "Export as PDF", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Text(text = "Itinerary for $tripName", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            if (days.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = days.indexOf(selectedDay).coerceAtLeast(0),
                    edgePadding = 24.dp,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = { tabPositions ->
                        val index = days.indexOf(selectedDay).coerceAtLeast(0)
                        if (index < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                ) {
                    days.forEach { day ->
                        Tab(
                            selected = selectedDay == day,
                            onClick = { selectedDay = day },
                            text = { Text("Day $day", style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
            ) {
                items(activities) { item ->
                    VoyageItineraryCard(item)
                }
            }
        }
    }
}

@Composable
fun VoyageItineraryCard(item: VoyageActivity) {
    val cardColor = when(item.type) {
        "Hotel", "Stay" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        "Restaurant" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val accentColor = when(item.type) {
        "Hotel", "Stay" -> MaterialTheme.colorScheme.primary
        "Restaurant" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.outline
    }
    
    val icon = when(item.type) {
        "Hotel", "Stay" -> Icons.Default.Hotel
        "Restaurant" -> Icons.Default.Restaurant
        else -> Icons.Default.AccessTime
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        // Enhanced Timeline dot/line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, 
            modifier = Modifier.width(32.dp).padding(top = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(accentColor)
                    .border(3.dp, Color.White, androidx.compose.foundation.shape.CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .weight(1f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(accentColor.copy(alpha = 0.5f), Color.Transparent)
                        )
                    )
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        AppCard(backgroundColor = cardColor) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = accentColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(icon, null, tint = accentColor, modifier = Modifier.padding(6.dp).size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = item.time, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                if (item.cost.isNotEmpty()) {
                    StatusBadge(text = item.cost, color = accentColor)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = item.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "${item.type} • ${item.category}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.description, 
                style = MaterialTheme.typography.bodyLarge, 
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ItineraryScreenPreview() {
    MyApplicationTheme {
        val sampleItinerary = mapOf(
            "1" to listOf(
                VoyageActivity("09:00 AM", "Eiffel Tower", "Sightseeing", "$30", "Visit Paris icon."),
                VoyageActivity("08:00 PM", "Hotel de Paris", "Stay", "$$$", "Rest up.", "Hotel")
            )
        )
        ItineraryScreen("Paris Trip", sampleItinerary)
    }
}
