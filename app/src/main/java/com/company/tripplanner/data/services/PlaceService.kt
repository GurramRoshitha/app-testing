package com.company.tripplanner.data.services

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class PlaceResult(
    val name: String,
    val type: String,
    val importance: Double = 0.0
)

class PlaceService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private suspend fun getCoordinates(cityName: String): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            try {
                // We add "India" or generic keywords to prioritize main cities
                val query = if (cityName.length < 15) "$cityName city" else cityName
                val url = "https://nominatim.openstreetmap.org/search?q=${query.replace(" ", "+")}&format=json&limit=1"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "VoyageTravelPlanner/1.0")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null
                    val body = response.body?.string() ?: return@withContext null
                    val jsonArray = JSONArray(body)
                    if (jsonArray.length() == 0) return@withContext null
                    
                    val firstResult = jsonArray.getJSONObject(0)
                    val lat = firstResult.getString("lat")
                    val lon = firstResult.getString("lon")
                    lat to lon
                }
            } catch (e: Exception) {
                Log.e("PlaceService", "Error geocoding city: $cityName", e)
                null
            }
        }
    }

    suspend fun getFamousPlaces(cityName: String): List<PlaceResult> {
        val coords = getCoordinates(cityName) ?: return emptyList()
        
        return withContext(Dispatchers.IO) {
            try {
                val (lat, lon) = coords
                // Improved Query: Filtering for high-importance tourism tags
                val overpassQuery = """
                    [out:json][timeout:30];
                    (
                      node["tourism"~"museum|attraction|monument|viewpoint|gallery|artwork|theme_park"](around:15000,$lat,$lon);
                      node["historic"~"castle|fort|ruins|monument|memorial|archaeological_site"](around:15000,$lat,$lon);
                      node["heritage"](around:15000,$lat,$lon);
                      way["tourism"~"museum|attraction|monument|viewpoint"](around:15000,$lat,$lon);
                    );
                    out body 30;
                """.trimIndent()

                val url = "https://overpass-api.de/api/interpreter?data=${overpassQuery.replace("\n", "")}"
                val request = Request.Builder().url(url).build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext emptyList()
                    val body = response.body?.string() ?: return@withContext emptyList()
                    val jsonObject = JSONObject(body)
                    val elements = jsonObject.getJSONArray("elements")
                    
                    val resultList = mutableListOf<PlaceResult>()
                    for (i in 0 until elements.length()) {
                        val element = elements.getJSONObject(i)
                        val tags = element.optJSONObject("tags") ?: continue
                        val name = tags.optString("name")
                        if (name.isNotEmpty() && name != "null") {
                            val type = tags.optString("tourism", tags.optString("historic", "Landmark"))
                            resultList.add(PlaceResult(name, type))
                        }
                    }
                    // Filter out very short names or non-landmark names
                    resultList.filter { it.name.length > 3 }.distinctBy { it.name }
                }
            } catch (e: Exception) {
                Log.e("PlaceService", "Error fetching attractions for: $cityName", e)
                emptyList()
            }
        }
    }

    suspend fun getRestaurants(cityName: String): List<String> {
        val coords = getCoordinates(cityName) ?: return emptyList()

        return withContext(Dispatchers.IO) {
            try {
                val (lat, lon) = coords
                // Search for established restaurants
                val overpassQuery = """
                    [out:json][timeout:25];
                    node["amenity"="restaurant"]["name"](around:8000,$lat,$lon);
                    out body 15;
                """.trimIndent()

                val url = "https://overpass-api.de/api/interpreter?data=${overpassQuery.replace("\n", "")}"
                val request = Request.Builder().url(url).build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext emptyList()
                    val body = response.body?.string() ?: return@withContext emptyList()
                    val jsonObject = JSONObject(body)
                    val elements = jsonObject.getJSONArray("elements")
                    
                    val resultList = mutableListOf<String>()
                    for (i in 0 until elements.length()) {
                        val element = elements.getJSONObject(i)
                        val tags = element.optJSONObject("tags") ?: continue
                        val name = tags.optString("name")
                        if (name.isNotEmpty() && name.length > 3) {
                            resultList.add(name)
                        }
                    }
                    resultList.distinct()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
