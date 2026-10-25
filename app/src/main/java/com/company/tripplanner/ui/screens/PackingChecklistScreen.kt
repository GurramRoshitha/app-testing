package com.company.tripplanner.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.data.models.PackingItem
import com.company.tripplanner.data.models.Trip
import com.company.tripplanner.data.services.WeatherInfo
import com.company.tripplanner.data.services.WeatherService
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.components.SectionHeader
import com.company.tripplanner.ui.theme.MyApplicationTheme
import com.company.tripplanner.utils.PackingRecommendationEngine
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingChecklistScreen(
    trip: Trip,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { context.getSharedPreferences("voyage_packing_prefs", Context.MODE_PRIVATE) }
    val jsonFormatter = remember { Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true } }
    val storageKey = "packing_list_${trip.id ?: trip.name}"

    val packingItems = remember { mutableStateListOf<PackingItem>() }
    var summaryBoxText by remember { mutableStateOf("Analyzing trip details...") }
    var weatherInfo by remember { mutableStateOf<WeatherInfo?>(null) }
    var isLoadingWeather by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }

    // Custom Item Form States
    var customName by remember { mutableStateOf("") }
    var customQuantity by remember { mutableStateOf("1") }
    var customCategory by remember { mutableStateOf("Travel Essentials") }

    val categories = listOf(
        "Clothing",
        "Travel Essentials",
        "Toiletries",
        "Weather Essentials",
        "Activity-Specific Items",
        "Documents and Safety"
    )

    // Save Checklist to Disk
    fun saveChecklist() {
        try {
            val jsonString = jsonFormatter.encodeToString(packingItems.toList())
            prefs.edit().putString(storageKey, jsonString).apply()
        } catch (e: Exception) {
            Log.e("PackingChecklist", "Error saving checklist", e)
        }
    }

    // Run Engine & Update Recommendations
    fun runEngineAndApply(weather: WeatherInfo? = weatherInfo) {
        val summary = PackingRecommendationEngine.generateRecommendations(
            trip = trip,
            weatherInfo = weather,
            existingItems = packingItems.toList()
        )
        summaryBoxText = summary.factorsSummary
        packingItems.clear()
        packingItems.addAll(summary.items)
        saveChecklist()
    }

    // Load Weather & Initialize Checklist
    LaunchedEffect(trip.id) {
        isLoadingWeather = true
        scope.launch {
            try {
                weatherInfo = WeatherService().getWeather(trip.name)
            } catch (e: Exception) {
                Log.e("PackingChecklist", "Weather load skipped/offline", e)
            } finally {
                isLoadingWeather = false

                // Load saved checklist or generate
                val savedJson = prefs.getString(storageKey, null)
                if (!savedJson.isNullOrEmpty()) {
                    try {
                        val list = jsonFormatter.decodeFromString<List<PackingItem>>(savedJson)
                        packingItems.clear()
                        packingItems.addAll(list)
                    } catch (e: Exception) {}
                }
                
                runEngineAndApply(weatherInfo)
            }
        }
    }

    // Progress Calculations
    val totalCount = packingItems.size
    val packedCount = packingItems.count { it.isPacked }
    val progressFraction = if (totalCount > 0) packedCount.toFloat() / totalCount else 0f
    val percentage = (progressFraction * 100).toInt()

    // Add Custom Item
    fun handleAddCustomItem() {
        if (customName.isBlank()) return
        val newItem = PackingItem(
            id = UUID.randomUUID().toString(),
            name = customName.trim(),
            category = customCategory,
            quantity = customQuantity.ifBlank { "1" },
            isPacked = false,
            isCustom = true
        )
        packingItems.add(newItem)
        saveChecklist()

        // Reset dialog form
        customName = ""
        customQuantity = "1"
        showAddDialog = false
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Custom Item", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Item Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customQuantity,
                        onValueChange = { customQuantity = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Text("Category:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    var expandedCat by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expandedCat = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(customCategory)
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                        }
                        DropdownMenu(
                            expanded = expandedCat,
                            onDismissRequest = { expandedCat = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        customCategory = cat
                                        expandedCat = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { handleAddCustomItem() }) {
                    Text("Add Item", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(trip.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(trip.dates, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { runEngineAndApply() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- ANALYSIS FACTORS SUMMARY CARD ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = summaryBoxText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- PROGRESS CARD ---
            AppCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Packing Progress",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$packedCount of $totalCount items packed",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "$percentage%",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ACTION BUTTONS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Item", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { runEngineAndApply() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Regenerate", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (packingItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Luggage,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Packing Items",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Click 'Regenerate' or 'Add Item' to build your list!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    categories.forEach { category ->
                        val catItems = packingItems.filter { it.category == category }
                        if (catItems.isNotEmpty()) {
                            item {
                                SectionHeader(title = category)
                            }
                            items(catItems, key = { it.id }) { item ->
                                PackingItemRow(
                                    item = item,
                                    onToggle = {
                                        val idx = packingItems.indexOfFirst { it.id == item.id }
                                        if (idx != -1) {
                                            packingItems[idx] = packingItems[idx].copy(isPacked = !item.isPacked)
                                            saveChecklist()
                                        }
                                    },
                                    onDelete = {
                                        packingItems.removeAll { it.id == item.id }
                                        saveChecklist()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PackingItemRow(
    item: PackingItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        color = if (item.isPacked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
        tonalElevation = if (item.isPacked) 0.dp else 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isPacked) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = item.isPacked,
                    onCheckedChange = { onToggle() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (item.isPacked) FontWeight.Normal else FontWeight.Bold,
                        textDecoration = if (item.isPacked) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (item.isPacked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                    )
                    if (item.quantity.isNotBlank()) {
                        Text(
                            text = "Qty: ${item.quantity}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete Item",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PackingChecklistScreenPreview() {
    MyApplicationTheme {
        PackingChecklistScreen(
            trip = Trip(name = "Goa Trip", dates = "Oct 24 - Oct 27, 2026", members = 2),
            onBack = {}
        )
    }
}
