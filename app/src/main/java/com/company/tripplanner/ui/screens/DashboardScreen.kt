package com.company.tripplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.components.SectionHeader
import com.company.tripplanner.ui.components.StatusBadge
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.theme.MyApplicationTheme
import com.company.tripplanner.ui.viewmodels.UserViewModel
import com.company.tripplanner.ui.viewmodels.WeatherViewModel
import com.company.tripplanner.data.models.Trip
import com.company.tripplanner.data.models.getEffectiveStatus
import java.text.SimpleDateFormat
import java.util.*

data class NavItem(val label: String, val icon: ImageVector, val route: String)

fun getDestinationImage(destinationName: String): String {
    val name = destinationName.lowercase()
    return when {
        name.contains("goa") -> "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=600&q=80"
        name.contains("munnar") || name.contains("kerala") -> "https://images.unsplash.com/photo-1593693397690-362cb9666fc2?auto=format&fit=crop&w=600&q=80"
        name.contains("ooty") -> "https://images.unsplash.com/photo-1544735716-392fe2489ffa?auto=format&fit=crop&w=600&q=80"
        name.contains("jaipur") -> "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=600&q=80"
        name.contains("telangana") || name.contains("hyderabad") -> "https://images.unsplash.com/photo-1605367031760-80236055d7f1?auto=format&fit=crop&w=600&q=80"
        name.contains("shimla") || name.contains("manali") || name.contains("kashmir") || name.contains("mountain") -> "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=600&q=80"
        name.contains("beach") || name.contains("andaman") -> "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=600&q=80"
        else -> "https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=600&q=80"
    }
}

@Composable
fun DashboardScreen(
    onActionClick: (String) -> Unit,
    onNavigateBottom: (String) -> Unit,
    userViewModel: UserViewModel,
    weatherViewModel: WeatherViewModel = viewModel(),
    latestTrip: Trip? = null,
    allTrips: List<Trip> = emptyList(),
    unreadNotificationsCount: Int = 0
) {
    val profile = userViewModel.userProfile.value
    val localWeather by weatherViewModel.localWeather
    val tripWeather by weatherViewModel.tripWeather

    val activeTrips = remember(allTrips, latestTrip) {
        val tripsToSearch = if (allTrips.isNotEmpty()) allTrips else listOfNotNull(latestTrip)
        tripsToSearch.filter { it.getEffectiveStatus() == "Active" }
    }

    val upcomingTrips = remember(allTrips, latestTrip) {
        val tripsToSearch = if (allTrips.isNotEmpty()) allTrips else listOfNotNull(latestTrip)
        tripsToSearch.filter { it.getEffectiveStatus() == "Upcoming" }
    }

    val completedTrips = remember(allTrips, latestTrip) {
        val tripsToSearch = if (allTrips.isNotEmpty()) allTrips else listOfNotNull(latestTrip)
        tripsToSearch.filter { it.getEffectiveStatus() == "Completed" }
    }

    val recentTrips = if (completedTrips.isNotEmpty()) completedTrips else remember(allTrips, activeTrips, upcomingTrips) {
        allTrips.filter { t -> activeTrips.none { it.id == t.id } && upcomingTrips.none { it.id == t.id } }
    }

    val featuredTrip = activeTrips.firstOrNull() ?: upcomingTrips.firstOrNull() ?: allTrips.firstOrNull()
    
    // Auto-load weather data
    LaunchedEffect(latestTrip) {
        weatherViewModel.loadLocalWeather("Chennai")
        latestTrip?.let { 
            weatherViewModel.loadTripWeather(it.name) 
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
                    selected = true,
                    onClick = { onNavigateBottom("home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Explore, contentDescription = "My Trips") },
                    label = { Text("My Trips", style = MaterialTheme.typography.labelSmall) },
                    selected = false,
                    onClick = { onNavigateBottom("trips") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                NavigationBarItem(
                    icon = {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Add, contentDescription = "Plan Trip", tint = Color.White)
                            }
                        }
                    },
                    label = { Text("Plan Trip", style = MaterialTheme.typography.labelSmall) },
                    selected = false,
                    onClick = { onActionClick("create") }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Backpack, contentDescription = "Packing") },
                    label = { Text("Packing", style = MaterialTheme.typography.labelSmall) },
                    selected = false,
                    onClick = { onActionClick("packing") }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
                    selected = false,
                    onClick = { onNavigateBottom("profile") }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateBottom("profile") }
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ) {
                            if (profile.profilePictureUri.isNotBlank()) {
                                AsyncImage(
                                    model = profile.profilePictureUri,
                                    contentDescription = "Profile",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = profile.fullName.take(1).ifBlank { "A" }.uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Good morning,",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${profile.fullName.split(" ").firstOrNull() ?: "Aarav"} 👋",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    IconButton(
                        onClick = { onActionClick("notifications") },
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = if (unreadNotificationsCount > 9) "9+" else "$unreadNotificationsCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // --- PLAN A NEW TRIP CARD BANNER ---
            item {
                Surface(
                    onClick = { onActionClick("create") },
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Plan a New Trip",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your ideas. A little AI. Endless possibility.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // --- 4 QUICK ACTION ICON PILLS ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionPill("My Trips", Icons.Default.Explore) { onActionClick("trips") }
                    QuickActionPill("Packing", Icons.Default.Backpack) { onActionClick("packing") }
                    QuickActionPill("Budget", Icons.Default.Calculate) { onActionClick("budget") }
                    QuickActionPill("Weather", Icons.Default.WbSunny) { onActionClick("weather_destination") }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- SECTION 1: CURRENT / ACTIVE TRIP ---
            if (activeTrips.isNotEmpty()) {
                item {
                    SectionHeader(title = "Current Trip")
                    activeTrips.forEach { trip ->
                        TripCountdownCard(
                            trip = trip,
                            onClick = { onActionClick("weather_destination") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // --- SECTION 2: UPCOMING TRIPS ---
            item {
                SectionHeader(title = "Upcoming Trips")
                if (upcomingTrips.isNotEmpty()) {
                    upcomingTrips.forEach { trip ->
                        TripCountdownCard(
                            trip = trip,
                            onClick = { onActionClick("weather_destination") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else if (activeTrips.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onActionClick("create") },
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TRIP COUNTDOWN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "No Upcoming Journeys",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Plan a trip to see your live departure countdown!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { onActionClick("create") },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "New Trip")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            // --- RECENT TRIPS SECTION (ACTUAL USER TRIPS) ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Trips",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = { onActionClick("trips") }) {
                        Text(
                            text = "View all",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (recentTrips.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        recentTrips.take(2).forEach { trip ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onActionClick("trips") },
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 2.dp,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            ) {
                                Column {
                                    AsyncImage(
                                        model = getDestinationImage(trip.name),
                                        contentDescription = trip.name,
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                    )
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = trip.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = trip.dates.ifBlank { "Completed trip" },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    AppCard(
                        backgroundColor = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = "No completed trips yet. Past journeys will appear here automatically!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionPill(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// --- TRIP COUNTDOWN CARD COMPONENT ---
@Composable
fun TripCountdownCard(
    trip: Trip,
    onClick: () -> Unit
) {
    val effectiveStatus = remember(trip) { trip.getEffectiveStatus() }
    val daysRemaining = remember(trip.dates) { calculateDaysRemaining(trip.dates) } ?: 0L
    val departureDateText = remember(trip.dates) { formatDepartureDate(trip.dates) }

    val daysSubtitleText = when {
        effectiveStatus == "Active" -> "Your adventure is underway!"
        daysRemaining > 1L -> "Your trip starts in $daysRemaining days!"
        daysRemaining == 1L -> "Your trip starts tomorrow!"
        daysRemaining == 0L -> "Your trip starts today!"
        else -> "Your adventure is underway!"
    }

    val progress = when {
        effectiveStatus == "Active" -> 0.5f
        daysRemaining <= 0L -> 1f
        daysRemaining >= 30L -> 0.15f
        else -> (30f - daysRemaining.toFloat()) / 30f
    }.coerceIn(0.1f, 1f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (effectiveStatus == "Active") "CURRENT TRIP" else "YOUR NEXT ADVENTURE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (effectiveStatus == "Active") Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = trip.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Dates: ${trip.dates.ifBlank { departureDateText }}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (effectiveStatus == "Active") Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = effectiveStatus.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = if (effectiveStatus == "Active") Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (effectiveStatus == "Active") Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = daysSubtitleText,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --- DATE CALCULATION UTILITIES ---
fun calculateDaysRemaining(datesString: String): Long? {
    if (datesString.isBlank()) return null
    val startDateStr = datesString.split("-").firstOrNull()?.trim() ?: datesString.trim()

    val todayCal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val formats = arrayOf(
        SimpleDateFormat("MMM dd, yyyy", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.US),
        SimpleDateFormat("dd/MM/yyyy", Locale.US),
        SimpleDateFormat("MM/dd/yyyy", Locale.US),
        SimpleDateFormat("MMM d, yyyy", Locale.US)
    )

    for (fmt in formats) {
        try {
            val parsed = fmt.parse(startDateStr)
            if (parsed != null) {
                val startCal = Calendar.getInstance().apply {
                    time = parsed
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val diffMillis = startCal.timeInMillis - todayCal.timeInMillis
                return diffMillis / (1000 * 60 * 60 * 24)
            }
        } catch (e: Exception) {}
    }
    return null
}

fun formatDepartureDate(datesString: String): String {
    if (datesString.isBlank()) return "TBD"
    val startDateStr = datesString.split("-").firstOrNull()?.trim() ?: datesString.trim()

    val inputFormats = arrayOf(
        SimpleDateFormat("MMM dd, yyyy", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.US),
        SimpleDateFormat("dd/MM/yyyy", Locale.US),
        SimpleDateFormat("MM/dd/yyyy", Locale.US),
        SimpleDateFormat("MMM d, yyyy", Locale.US)
    )
    val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)

    for (fmt in inputFormats) {
        try {
            val parsed = fmt.parse(startDateStr)
            if (parsed != null) {
                return outputFormat.format(parsed)
            }
        } catch (e: Exception) {}
    }
    return startDateStr
}

private fun getWeatherIcon(condition: String): ImageVector {
    return when {
        condition.contains("Sunny", ignoreCase = true) || condition.contains("Clear", ignoreCase = true) -> Icons.Default.WbSunny
        condition.contains("Cloudy", ignoreCase = true) -> Icons.Default.Cloud
        condition.contains("Rain", ignoreCase = true) || condition.contains("Drizzle", ignoreCase = true) -> Icons.Default.WaterDrop
        condition.contains("Thunderstorm", ignoreCase = true) -> Icons.Default.Thunderstorm
        else -> Icons.Default.Cloud
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    MyApplicationTheme {
        DashboardScreen(onActionClick = {}, onNavigateBottom = {}, userViewModel = UserViewModel(), latestTrip = null)
    }
}
