package com.company.tripplanner.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.data.models.Trip
import java.util.Locale
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.theme.MyApplicationTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
data class SharedExpense(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val totalCost: Double = 0.0,
    val travelersCount: Int = 1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    trip: Trip? = null,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("voyage_budget_prefs", Context.MODE_PRIVATE) }
    val jsonFormatter = remember { Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true } }
    val storageKey = "group_expenses_${trip?.id ?: "default"}"

    val expenses = remember { mutableStateListOf<SharedExpense>() }
    var showAddDialog by remember { mutableStateOf(false) }

    var inputTitle by remember { mutableStateOf("") }
    var inputCost by remember { mutableStateOf("") }
    var inputTravelers by remember { mutableStateOf(trip?.members?.toString() ?: "3") }

    // Save Expenses to Disk
    fun saveExpenses() {
        try {
            val jsonString = jsonFormatter.encodeToString(expenses.toList())
            prefs.edit().putString(storageKey, jsonString).apply()
        } catch (e: Exception) {
            Log.e("BudgetScreen", "Error saving expenses", e)
        }
    }

    // Load Expenses
    LaunchedEffect(trip?.id) {
        try {
            val savedJson = prefs.getString(storageKey, null)
            if (!savedJson.isNullOrEmpty()) {
                val list = jsonFormatter.decodeFromString<List<SharedExpense>>(savedJson)
                expenses.clear()
                expenses.addAll(list)
            } else {
                val defaults = listOf(
                    SharedExpense(title = "cab", totalCost = 3500.0, travelersCount = 3),
                    SharedExpense(title = "hotel", totalCost = 5500.0, travelersCount = 3),
                    SharedExpense(title = "food", totalCost = 4500.0, travelersCount = 2)
                )
                expenses.clear()
                expenses.addAll(defaults)
                saveExpenses()
            }
        } catch (e: Exception) {
            Log.e("BudgetScreen", "Error loading expenses", e)
        }
    }

    val totalGroupCost = expenses.sumOf { it.totalCost }
    val sharePerPerson = expenses.sumOf {
        if (it.travelersCount > 0) it.totalCost / it.travelersCount else it.totalCost
    }

    // Add Expense
    fun handleAddExpense() {
        val costVal = inputCost.toDoubleOrNull() ?: 0.0
        val travVal = inputTravelers.toIntOrNull() ?: 1
        if (inputTitle.isBlank() || costVal <= 0) return

        val newExp = SharedExpense(
            title = inputTitle.trim().lowercase(),
            totalCost = costVal,
            travelersCount = travVal
        )
        expenses.add(newExp)
        saveExpenses()

        inputTitle = ""
        inputCost = ""
        showAddDialog = false
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("Expense Title (e.g. cab, hotel, food)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = inputCost,
                        onValueChange = { inputCost = it },
                        label = { Text("Total Cost (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = inputTravelers,
                        onValueChange = { inputTravelers = it },
                        label = { Text("Number of Travelers") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = { handleAddExpense() }) {
                    Text("Add Expense", fontWeight = FontWeight.Bold)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Group Cost Split", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                            Text("Split shared expenses equally among travelers", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Expense", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

            // --- SUMMARY CARD ---
            AppCard(
                backgroundColor = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL GROUP COST",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${String.format(Locale.US, "%,.2f", totalGroupCost)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "SHARE PER PERSON",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${String.format(Locale.US, "%,.2f", sharePerPerson)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- EXPENSES LIST ---
            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No shared expenses added yet. Tap '+ Add Expense'!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(expenses, key = { it.id }) { item ->
                        val perPerson = if (item.travelersCount > 0) item.totalCost / item.travelersCount else item.totalCost

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Total: ₹${String.format("%,.2f", item.totalCost)} • ${item.travelersCount} Travelers",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹${String.format("%,.2f", perPerson)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "PER PERSON",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            fontSize = 9.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = {
                                            expenses.removeIf { it.id == item.id }
                                            saveExpenses()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BudgetScreenPreview() {
    MyApplicationTheme {
        BudgetScreen(trip = Trip(name = "Munnar escape", members = 3))
    }
}
