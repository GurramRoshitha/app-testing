package com.company.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.theme.MyApplicationTheme

@Composable
fun FavoritesScreen() {
    val favoriteDestinations = listOf("Paris, France", "Tokyo, Japan", "London, UK", "New York, USA")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Favorites", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(favoriteDestinations) { destination ->
                FavoriteCard(destination)
            }
        }
    }
}

@Composable
fun FavoriteCard(destination: String) {
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = destination, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            IconButton(onClick = { /* Remove from favorites */ }) {
                Icon(Icons.Default.Favorite, contentDescription = "Remove", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FavoritesScreenPreview() {
    MyApplicationTheme {
        FavoritesScreen()
    }
}
