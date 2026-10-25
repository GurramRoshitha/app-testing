package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun DestinationSearchScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val results = listOf("London, UK", "New York, USA", "Berlin, Germany", "Tokyo, Japan")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search destinations...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(results.size) { index ->
                SearchResultCard(results[index])
            }
        }
    }
}

@Composable
fun SearchResultCard(destination: String) {
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = destination, fontSize = 18.sp)
            IconButton(onClick = { }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Add to favorites")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DestinationSearchScreenPreview() {
    MyApplicationTheme {
        DestinationSearchScreen()
    }
}
