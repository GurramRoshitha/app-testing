package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun AppUpdateScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "New Version Available", style = MaterialTheme.typography.displayLarge, fontSize = 28.sp, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "We've added new features and fixed some bugs to make your trip planning even better.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(text = "Version 1.2.0 (15 MB)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(48.dp))
        AppButton(text = "Update Now", onClick = { })
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = { }) {
            Text("Later", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppUpdateScreenPreview() {
    MyApplicationTheme {
        AppUpdateScreen()
    }
}
