package com.company.tripplanner.data.services

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class WeatherInfo(
    val temp: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val dailyForecast: List<DailyForecast>
)

data class DailyForecast(
    val day: String,
    val temp: Double,
    val condition: String
)

class WeatherService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun getWeather(cityName: String): WeatherInfo? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Get Coordinates using Nominatim
                val geoUrl = "https://nominatim.openstreetmap.org/search?q=${cityName.replace(" ", "+")}&format=json&limit=1"
                val geoRequest = Request.Builder().url(geoUrl).header("User-Agent", "VoyageTravelPlanner/1.0").build()
                val geoBody = client.newCall(geoRequest).execute().use { it.body?.string() } ?: return@withContext null
                val geoArray = JSONArray(geoBody)
                if (geoArray.length() == 0) return@withContext null
                
                val lat = geoArray.getJSONObject(0).getDouble("lat")
                val lon = geoArray.getJSONObject(0).getDouble("lon")

                // 2. Get Weather using Open-Meteo (Free)
                val weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code&daily=weather_code,temperature_2m_max&timezone=auto"
                val weatherRequest = Request.Builder().url(weatherUrl).build()
                val weatherBody = client.newCall(weatherRequest).execute().use { it.body?.string() } ?: return@withContext null
                val weatherJson = JSONObject(weatherBody)

                val current = weatherJson.getJSONObject("current")
                val daily = weatherJson.getJSONObject("daily")
                val timeArray = daily.getJSONArray("time")
                val tempMaxArray = daily.getJSONArray("temperature_2m_max")
                val codeArray = daily.getJSONArray("weather_code")

                val forecasts = mutableListOf<DailyForecast>()
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                
                for (i in 0 until 7) {
                    forecasts.add(DailyForecast(
                        day = days[i % 7], // Simplified for now
                        temp = tempMaxArray.getDouble(i),
                        condition = getWeatherCondition(codeArray.getInt(i))
                    ))
                }

                WeatherInfo(
                    temp = current.getDouble("temperature_2m"),
                    condition = getWeatherCondition(current.getInt("weather_code")),
                    humidity = current.getInt("relative_humidity_2m"),
                    windSpeed = current.getDouble("wind_speed_10m"),
                    dailyForecast = forecasts
                )
            } catch (e: Exception) {
                Log.e("WeatherService", "Error fetching weather", e)
                null
            }
        }
    }

    private fun getWeatherCondition(code: Int): String {
        return when (code) {
            0 -> "Clear Sky"
            1, 2, 3 -> "Partly Cloudy"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rainy"
            71, 73, 75 -> "Snowy"
            95, 96, 99 -> "Thunderstorm"
            else -> "Cloudy"
        }
    }
}
