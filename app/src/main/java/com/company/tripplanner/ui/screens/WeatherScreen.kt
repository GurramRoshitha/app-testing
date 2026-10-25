package com.company.tripplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.tripplanner.ui.components.AppCard
import com.company.tripplanner.ui.theme.MyApplicationTheme
import com.company.tripplanner.ui.viewmodels.WeatherViewModel

@Composable
fun WeatherScreen(destination: String = "Lisbon, Portugal", weatherViewModel: WeatherViewModel = viewModel()) {
    val weatherInfo by weatherViewModel.weatherState
    val isLoading by weatherViewModel.isLoading

    LaunchedEffect(destination) {
        weatherViewModel.loadWeather(destination)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Sky Forecast", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Text(text = "Weather for $destination", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (weatherInfo != null) {
            val info = weatherInfo!!
            AppCard(
                backgroundColor = Color.Transparent,
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = getWeatherIcon(info.condition), 
                        contentDescription = null, 
                        modifier = Modifier.size(72.dp), 
                        tint = Color.White
                    )
                    Text(text = "${info.temp.toInt()}°C", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = info.condition, style = MaterialTheme.typography.headlineMedium, color = Color.White.copy(alpha = 0.9f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Humidity: ${info.humidity}% • Wind: ${info.windSpeed} km/h", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            Text(text = "7-Day Outlook", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(end = 24.dp)
            ) {
                items(info.dailyForecast) { day ->
                    WeatherDayItem(day.day, "${day.temp.toInt()}°C", getWeatherIcon(day.condition))
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("Weather data unavailable", color = Color.Gray)
            }
        }
    }
}

private fun getWeatherIcon(condition: String): ImageVector {
    return when {
        condition.contains("Sunny", ignoreCase = true) || condition.contains("Clear", ignoreCase = true) -> Icons.Default.WbSunny
        condition.contains("Cloudy", ignoreCase = true) -> Icons.Default.Cloud
        condition.contains("Rain", ignoreCase = true) || condition.contains("Drizzle", ignoreCase = true) -> Icons.Default.WaterDrop
        condition.contains("Thunderstorm", ignoreCase = true) -> Icons.Default.Thunderstorm
        else -> Icons.Default.Cloud
    }
}

@Composable
fun WeatherDayItem(day: String, temp: String, icon: ImageVector) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.width(80.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(text = day, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = if (icon == Icons.Default.WbSunny) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = temp, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WeatherScreenPreview() {
    MyApplicationTheme {
        WeatherScreen("London, UK")
    }
}
