package com.company.tripplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.components.AppInputField
import com.company.tripplanner.ui.components.SectionHeader
import com.company.tripplanner.ui.theme.MyApplicationTheme
import com.company.tripplanner.ui.viewmodels.TripViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    tripViewModel: TripViewModel? = null,
    onSave: (String, String, String, Int, Double, String) -> Unit
) {
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    
    // Use isGenerating from ViewModel if available, otherwise local state
    val isGeneratingViewModel = tripViewModel?.isGenerating?.value ?: false
    var isGeneratingLocal by remember { mutableStateOf(false) }
    val isGenerating = isGeneratingViewModel || isGeneratingLocal

    val scrollState = rememberScrollState()
    
    // Date Picker States
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var isSelectingStartDate by remember { mutableStateOf(true) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        formatter.format(Date(it))
                    } ?: ""
                    if (isSelectingStartDate) startDate = date else endDate = date
                    showDatePicker = false
                }) {
                    Text("OK", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (isGenerating) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 6.dp)
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Voyage AI is analyzing local maps...", 
                    style = MaterialTheme.typography.displaySmall, 
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Curating the best experiences for you in $destination",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
            }
        }
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                        AppButton(
                            text = "Generate Journey", 
                            onClick = { 
                                if (destination.isNotEmpty()) {
                                    isGeneratingLocal = true
                                    onSave(
                                        destination, 
                                        startDate, 
                                        endDate, 
                                        1,
                                        0.0,
                                        "USD"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Plan New Journey", 
                    style = MaterialTheme.typography.displayMedium, 
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "AI will craft your perfect itinerary based on your preferences.", 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                SectionHeader("Destination")
                AppInputField(
                    value = destination, 
                    onValueChange = { destination = it }, 
                    label = "Where to?",
                    leadingIcon = { Icon(Icons.Default.Map, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        AppInputField(
                            value = startDate, 
                            onValueChange = { }, 
                            label = "Start Date",
                            trailingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { 
                            isSelectingStartDate = true
                            showDatePicker = true 
                        })
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        AppInputField(
                            value = endDate, 
                            onValueChange = { }, 
                            label = "End Date",
                            trailingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { 
                            isSelectingStartDate = false
                            showDatePicker = true 
                        })
                    }
                }
                
                Spacer(modifier = Modifier.height(120.dp)) // Cushion for bottom bar
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateTripScreenPreview() {
    MyApplicationTheme {
        CreateTripScreen(onSave = { _, _, _, _, _, _ -> })
    }
}
