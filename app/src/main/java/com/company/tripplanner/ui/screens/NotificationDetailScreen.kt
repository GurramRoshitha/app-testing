package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Details", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(24.dp)
        ) {
            Text(text = "Trip Alert", style = MaterialTheme.typography.displayLarge, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
            Text(text = "1 hour ago", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Your flight AF234 to Paris departs in 24 hours from Terminal 2E. Don't forget to check in online and arrive at the airport at least 3 hours early.",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 26.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            AppButton(text = "View Trip Details", onClick = { })
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationDetailScreenPreview() {
    MyApplicationTheme {
        NotificationDetailScreen(onBack = {})
    }
}
