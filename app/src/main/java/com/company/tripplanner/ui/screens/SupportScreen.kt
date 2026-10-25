package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.components.AppInputField
import com.company.tripplanner.ui.components.SectionHeader
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun SupportScreen() {
    var feedback by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Help & Support", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader("FAQs")
        Text(text = "• How do I plan a trip?\n• Can I sync my calendar?\n• How to manage budget?", lineHeight = 32.sp)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SectionHeader("Feedback")
        AppInputField(value = feedback, onValueChange = { feedback = it }, label = "How can we improve?", modifier = Modifier.height(150.dp))
        
        Spacer(modifier = Modifier.height(24.dp))
        AppButton(text = "Send Feedback", onClick = { })
        
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Text(text = "Contact Support")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SupportScreenPreview() {
    MyApplicationTheme {
        SupportScreen()
    }
}
