package com.company.tripplanner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.components.AppInputField
import com.company.tripplanner.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportDetailsScreen(onSave: () -> Unit) {
    var transportType by remember { mutableStateOf("Flight") }
    var bookingNumber by remember { mutableStateOf("") }
    var departureDate by remember { mutableStateOf("") }
    var arrivalDate by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var isSelectingDeparture by remember { mutableStateOf(true) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        formatter.format(Date(it))
                    } ?: ""
                    if (isSelectingDeparture) departureDate = date else arrivalDate = date
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Transport Details", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        AppInputField(value = transportType, onValueChange = { transportType = it }, label = "Transport Type (Flight/Train/Bus/Car)")
        Spacer(modifier = Modifier.height(16.dp))
        AppInputField(value = bookingNumber, onValueChange = { bookingNumber = it }, label = "Booking Number")
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                AppInputField(
                    value = departureDate, 
                    onValueChange = { }, 
                    label = "Departure",
                    trailingIcon = { Icon(Icons.Default.CalendarMonth, null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { 
                    isSelectingDeparture = true
                    showDatePicker = true 
                })
            }
            Box(modifier = Modifier.weight(1f)) {
                AppInputField(
                    value = arrivalDate, 
                    onValueChange = { }, 
                    label = "Arrival",
                    trailingIcon = { Icon(Icons.Default.CalendarMonth, null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { 
                    isSelectingDeparture = false
                    showDatePicker = true 
                })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AppInputField(value = cost, onValueChange = { cost = it }, label = "Cost ($)")

        Spacer(modifier = Modifier.weight(1f))
        AppButton(text = "Save Transport", onClick = onSave)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransportDetailsScreenPreview() {
    MyApplicationTheme {
        TransportDetailsScreen(onSave = {})
    }
}
