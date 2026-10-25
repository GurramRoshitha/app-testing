package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppButton
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.theme.MyApplicationTheme

data class Hotel(val name: String, val rating: String, val price: String)

@Composable
fun HotelSearchScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val hotels = listOf(
        Hotel("Grand Plaza Hotel", "4.8", "$200/night"),
        Hotel("Ocean View Resort", "4.5", "$150/night"),
        Hotel("City Central Inn", "4.2", "$100/night"),
        Hotel("Mountain Lodge", "4.7", "$180/night")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search hotels...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(hotels) { hotel ->
                HotelCard(hotel)
            }
        }
    }
}

@Composable
fun HotelCard(hotel: Hotel) {
    AppCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = hotel.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Text(text = hotel.rating, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
            Text(text = hotel.price, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            AppButton(text = "View Details", onClick = { }, modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HotelSearchScreenPreview() {
    MyApplicationTheme {
        HotelSearchScreen()
    }
}
