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
fun AccommodationDetailsScreen(onSave: () -> Unit) {
    var hotelName by remember { mutableStateOf("") }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var isSelectingCheckIn by remember { mutableStateOf(true) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        formatter.format(Date(it))
                    } ?: ""
                    if (isSelectingCheckIn) checkIn = date else checkOut = date
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
        Text(text = "Accommodation", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        AppInputField(value = hotelName, onValueChange = { hotelName = it }, label = "Hotel Name")
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                AppInputField(
                    value = checkIn, 
                    onValueChange = { }, 
                    label = "Check-in",
                    trailingIcon = { Icon(Icons.Default.CalendarMonth, null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { 
                    isSelectingCheckIn = true
                    showDatePicker = true 
                })
            }
            Box(modifier = Modifier.weight(1f)) {
                AppInputField(
                    value = checkOut, 
                    onValueChange = { }, 
                    label = "Check-out",
                    trailingIcon = { Icon(Icons.Default.CalendarMonth, null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { 
                    isSelectingCheckIn = false
                    showDatePicker = true 
                })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AppInputField(value = address, onValueChange = { address = it }, label = "Address")
        Spacer(modifier = Modifier.height(16.dp))
        AppInputField(value = cost, onValueChange = { cost = it }, label = "Total Cost ($)")

        Spacer(modifier = Modifier.weight(1f))
        AppButton(text = "Save Accommodation", onClick = onSave)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AccommodationDetailsScreenPreview() {
    MyApplicationTheme {
        AccommodationDetailsScreen(onSave = {})
    }
}
