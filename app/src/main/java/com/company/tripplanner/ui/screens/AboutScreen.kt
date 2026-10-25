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
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "About", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        
        Icon(
            painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_dialog_info),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Trip Planner AI", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "Version 1.0.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Your personalized AI-powered travel companion. Plan, manage, and explore the world with ease.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Developed by", fontWeight = FontWeight.SemiBold)
        Text(text = "AI Travel Solutions Team", color = MaterialTheme.colorScheme.primary)
        
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = { /* Navigate to Privacy Policy */ }) {
            Text(text = "Privacy Policy")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AboutScreenPreview() {
    MyApplicationTheme {
        AboutScreen()
    }
}
