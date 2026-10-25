package com.company.tripplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.components.SectionHeader
import com.company.tripplanner.ui.theme.MyApplicationTheme

data class VoyagePlace(
    val name: String, 
    val distance: String, 
    val cost: String, 
    val description: String,
    val tier: String
)

@Composable
fun NearbyPlacesScreen() {
    val places = listOf(
        VoyagePlace("LX Factory", "3.2 km", "$", "Creative hub in an industrial complex with shops and cafes.", "Budget"),
        VoyagePlace("Pink Street", "0.5 km", "$", "Famous nightlife street known for its colored pavement.", "Budget"),
        VoyagePlace("Sintra Day Trip", "25 km", "$$$", "Magical town with fairytale palaces and ancient castles.", "Premium"),
        VoyagePlace("Oceanário de Lisboa", "8 km", "$$", "One of the world's largest and most impressive aquariums.", "Mid"),
        VoyagePlace("Cascais Beach", "30 km", "$$", "Charming coastal town perfect for a relaxing seaside afternoon.", "Mid"),
        VoyagePlace("Yacht Sunset Cruise", "2 km", "$$$", "Luxury sailing experience on the Tagus at golden hour.", "Premium")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Nearby Gems", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        }
        Text(text = "Hand-picked recommendations for you", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            listOf("Budget", "Mid", "Premium").forEach { tier ->
                item {
                    SectionHeader(title = "$tier Tier")
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        places.filter { it.tier == tier }.forEach { place ->
                            NearbyPlaceCard(place)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NearbyPlaceCard(place: VoyagePlace) {
    AppCard(backgroundColor = Color.White) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = place.name, style = MaterialTheme.typography.headlineMedium, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Surface(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(100.dp)
            ) {
                Text(
                    text = place.cost, 
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            Text(text = place.distance, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = place.description,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NearbyPlacesScreenPreview() {
    MyApplicationTheme {
        NearbyPlacesScreen()
    }
}
