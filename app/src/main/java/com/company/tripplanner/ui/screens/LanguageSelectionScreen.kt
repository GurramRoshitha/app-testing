package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun LanguageSelectionScreen(onSave: () -> Unit) {
    val languages = listOf("English", "Spanish", "French", "German", "Chinese", "Japanese", "Arabic", "Hindi")
    var searchQuery by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("English") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Select Language", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search language...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(languages.filter { it.contains(searchQuery, ignoreCase = true) }) { lang ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(text = lang, fontSize = 18.sp)
                    RadioButton(
                        selected = selectedLanguage == lang,
                        onClick = { selectedLanguage = lang }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        AppButton(text = "Save Selection", onClick = onSave)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LanguageSelectionScreenPreview() {
    MyApplicationTheme {
        LanguageSelectionScreen(onSave = {})
    }
}
