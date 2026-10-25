package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun NotificationSettingsScreen(onSave: () -> Unit) {
    var tripReminders by remember { mutableStateOf(true) }
    var budgetAlerts by remember { mutableStateOf(true) }
    var activityNotifications by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Notifications", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        NotificationToggle("Trip Reminders", "Get notified about upcoming departures", tripReminders) { tripReminders = it }
        NotificationToggle("Budget Alerts", "Get notified when you exceed budget", budgetAlerts) { budgetAlerts = it }
        NotificationToggle("Activity Updates", "Get notified about itinerary changes", activityNotifications) { activityNotifications = it }

        Spacer(modifier = Modifier.weight(1f))
        AppButton(text = "Save Settings", onClick = onSave)
    }
}

@Composable
fun NotificationToggle(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(text = description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationSettingsScreenPreview() {
    MyApplicationTheme {
        NotificationSettingsScreen(onSave = {})
    }
}
