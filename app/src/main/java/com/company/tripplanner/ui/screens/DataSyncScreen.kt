package com.company.tripplanner.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun DataSyncScreen() {
    var isSyncing by remember { mutableStateOf(false) }
    val rotation = rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "syncRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Data Sync", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(48.dp))
        
        Icon(
            Icons.Default.Sync,
            contentDescription = null,
            modifier = Modifier.size(80.dp).rotate(if(isSyncing) rotation.value else 0f),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = if(isSyncing) "Syncing your data..." else "Everything is up to date", fontWeight = FontWeight.Medium)
        Text(text = "Last synced: 2 minutes ago", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(64.dp))
        AppButton(
            text = "Sync Now",
            onClick = { isSyncing = !isSyncing },
            modifier = Modifier.padding(horizontal = 48.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DataSyncScreenPreview() {
    MyApplicationTheme {
        DataSyncScreen()
    }
}
