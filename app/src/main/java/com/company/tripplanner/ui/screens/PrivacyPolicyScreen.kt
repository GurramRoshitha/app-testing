package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            Text(text = "How We Use Your Data", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "This Privacy Policy describes how your personal information is collected, used, and shared when you use the Trip Planner AI mobile application.\n\n" +
                        "1. DATA COLLECTION\n" +
                        "We collect information that you provide directly to us when you create an account, plan a trip, or communicate with us.\n\n" +
                        "2. HOW WE USE INFORMATION\n" +
                        "We use the information we collect to provide, maintain, and improve our services, including to provide personalized trip recommendations.\n\n" +
                        "3. DATA SECURITY\n" +
                        "We take reasonable measures to help protect information about you from loss, theft, misuse and unauthorized access.",
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrivacyPolicyScreenPreview() {
    MyApplicationTheme {
        PrivacyPolicyScreen(onBack = {})
    }
}
