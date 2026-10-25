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
fun AddActivityScreen(onSave: () -> Unit) {
    var activityName by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    date = datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        formatter.format(Date(it))
                    } ?: ""
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
        Text(text = "Add Activity", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        AppInputField(value = activityName, onValueChange = { activityName = it }, label = "Activity Name")
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                AppInputField(
                    value = date, 
                    onValueChange = { }, 
                    label = "Date",
                    trailingIcon = { Icon(Icons.Default.CalendarMonth, null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
            }
            AppInputField(value = time, onValueChange = { time = it }, label = "Time", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        AppInputField(value = notes, onValueChange = { notes = it }, label = "Notes", modifier = Modifier.height(120.dp))

        Spacer(modifier = Modifier.weight(1f))
        AppButton(text = "Save Activity", onClick = onSave)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddActivityScreenPreview() {
    MyApplicationTheme {
        AddActivityScreen(onSave = {})
    }
}
