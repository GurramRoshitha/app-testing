package com.company.tripplanner.utils

import com.company.tripplanner.data.models.PackingItem
import com.company.tripplanner.data.models.Trip
import com.company.tripplanner.data.services.WeatherInfo
import java.text.SimpleDateFormat
import java.util.*

data class RecommendationAnalysisSummary(
    val factorsSummary: String,
    val items: List<PackingItem>
)

object PackingRecommendationEngine {

    fun generateRecommendations(
        trip: Trip,
        weatherInfo: WeatherInfo? = null,
        existingItems: List<PackingItem> = emptyList()
    ): RecommendationAnalysisSummary {
        // 1. Run Individual Analyzers
        val destResult = DestinationAnalyzer.analyze(trip.name)
        val seasonResult = SeasonalAnalyzer.analyze(trip.dates, destResult.terrainType)
        val itineraryResult = ItineraryAnalyzer.analyze(trip.itinerary)
        val dayCount = calculateDurationInDays(trip.dates)

        // 2. Build Recommendation Summary Box String
        val weatherText = if (weatherInfo != null) {
            "${weatherInfo.temp.toInt()}°C ${weatherInfo.condition}"
        } else {
            "Historical Seasonal Forecast"
        }

        val analysisSummary = "Analysis: ${destResult.terrainType.label} • ${seasonResult.seasonType.label} • $weatherText • ${itineraryResult.summaryText}"

        // 3. Generate Items
        val generatedItems = mutableListOf<PackingItem>()

        // --- CATEGORY 1: CLOTHING ---
        val shirtCount = (dayCount + 1).coerceAtMost(8)
        val pantsCount = Math.max(2, (dayCount / 2) + 1)
        val undergarmentCount = (dayCount + 1).coerceAtMost(8)
        val socksCount = (dayCount + 1).coerceAtMost(7)

        generatedItems.add(PackingItem(name = "Shirts / Tops", category = "Clothing", quantity = "$shirtCount"))
        generatedItems.add(PackingItem(name = "Pants / Trousers / Shorts", category = "Clothing", quantity = "$pantsCount"))
        generatedItems.add(PackingItem(name = "Undergarments", category = "Clothing", quantity = "$undergarmentCount"))
        generatedItems.add(PackingItem(name = "Socks", category = "Clothing", quantity = "$socksCount pairs"))
        generatedItems.add(PackingItem(name = "Sleepwear / Nightwear", category = "Clothing", quantity = "${Math.max(1, dayCount / 2)} set(s)"))

        if (destResult.terrainType == TerrainType.MOUNTAIN || seasonResult.seasonType == SeasonType.WINTER || (weatherInfo != null && weatherInfo.temp < 18.0)) {
            generatedItems.add(PackingItem(name = "Warm Jacket / Heavy Sweater", category = "Clothing", quantity = "1-2"))
            generatedItems.add(PackingItem(name = "Thermal Innerwear", category = "Clothing", quantity = "1-2 sets"))
        }

        if (destResult.terrainType == TerrainType.COASTAL || itineraryResult.hasBeach) {
            generatedItems.add(PackingItem(name = "Swimwear / Beachwear", category = "Clothing", quantity = "2 sets"))
            generatedItems.add(PackingItem(name = "Breathable Cotton Tops", category = "Clothing", quantity = "$dayCount"))
        }

        if (destResult.terrainType == TerrainType.PILGRIMAGE || itineraryResult.hasReligious) {
            generatedItems.add(PackingItem(name = "Modest / Traditional Attire", category = "Clothing", quantity = "2-3 sets"))
        }

        // --- CATEGORY 2: TRAVEL ESSENTIALS ---
        generatedItems.add(PackingItem(name = "Backpack / Daypack", category = "Travel Essentials", quantity = "1"))
        generatedItems.add(PackingItem(name = "Mobile Phone & Charger", category = "Travel Essentials", quantity = "1"))
        generatedItems.add(PackingItem(name = "Power Bank", category = "Travel Essentials", quantity = "1"))
        generatedItems.add(PackingItem(name = "Reusable Water Bottle", category = "Travel Essentials", quantity = "1"))
        generatedItems.add(PackingItem(name = "Sunglasses", category = "Travel Essentials", quantity = "1"))
        generatedItems.add(PackingItem(name = "Compact Travel Umbrella", category = "Travel Essentials", quantity = "1"))

        // --- CATEGORY 3: TOILETRIES ---
        generatedItems.add(PackingItem(name = "Toothbrush & Toothpaste", category = "Toiletries", quantity = "1 kit"))
        generatedItems.add(PackingItem(name = "Shampoo & Shower Gel", category = "Toiletries", quantity = "1 travel kit"))
        generatedItems.add(PackingItem(name = "Face Wash & Moisturizer", category = "Toiletries", quantity = "1 set"))
        generatedItems.add(PackingItem(name = "Deodorant / Perfume", category = "Toiletries", quantity = "1"))
        generatedItems.add(PackingItem(name = "Hand Sanitizer & Wet Wipes", category = "Toiletries", quantity = "1 pack"))

        // --- CATEGORY 4: WEATHER ESSENTIALS ---
        val conditionLower = weatherInfo?.condition?.lowercase() ?: ""
        val isRainy = conditionLower.contains("rain") || conditionLower.contains("drizzle") || conditionLower.contains("thunderstorm") || seasonResult.seasonType == SeasonType.MONSOON

        if (isRainy) {
            generatedItems.add(PackingItem(name = "Raincoat / Rain Poncho", category = "Weather Essentials", quantity = "1"))
            generatedItems.add(PackingItem(name = "Waterproof Bag Cover", category = "Weather Essentials", quantity = "1"))
        }

        if (destResult.terrainType == TerrainType.COASTAL || destResult.terrainType == TerrainType.DESERT || seasonResult.seasonType == SeasonType.SUMMER || (weatherInfo != null && weatherInfo.temp > 28.0)) {
            generatedItems.add(PackingItem(name = "Sunscreen Lotion (SPF 50+)", category = "Weather Essentials", quantity = "1 bottle"))
            generatedItems.add(PackingItem(name = "Lip Balm with SPF", category = "Weather Essentials", quantity = "1"))
            generatedItems.add(PackingItem(name = "Wide-Brim Hat / Cap", category = "Weather Essentials", quantity = "1"))
        }

        if (destResult.terrainType == TerrainType.MOUNTAIN || seasonResult.seasonType == SeasonType.WINTER || (weatherInfo != null && weatherInfo.temp < 15.0)) {
            generatedItems.add(PackingItem(name = "Cold Cream & Lip Balm", category = "Weather Essentials", quantity = "1 tube"))
            generatedItems.add(PackingItem(name = "Woolen Cap & Gloves", category = "Weather Essentials", quantity = "1 pair"))
        }

        // --- CATEGORY 5: ACTIVITY-SPECIFIC ITEMS ---
        if (itineraryResult.hasTrekking || destResult.terrainType == TerrainType.MOUNTAIN) {
            generatedItems.add(PackingItem(name = "Trekking Boots / Hiking Shoes", category = "Activity-Specific Items", quantity = "1 pair"))
            generatedItems.add(PackingItem(name = "Flashlight / Torch", category = "Activity-Specific Items", quantity = "1"))
            generatedItems.add(PackingItem(name = "Energy Bars & First Aid", category = "Activity-Specific Items", quantity = "1 pack"))
        }

        if (itineraryResult.hasBeach || destResult.terrainType == TerrainType.COASTAL) {
            generatedItems.add(PackingItem(name = "Flip-Flops / Beach Sandals", category = "Activity-Specific Items", quantity = "1 pair"))
            generatedItems.add(PackingItem(name = "Waterproof Phone Case", category = "Activity-Specific Items", quantity = "1"))
            generatedItems.add(PackingItem(name = "Quick-Dry Microfiber Towel", category = "Activity-Specific Items", quantity = "1"))
        }

        if (itineraryResult.hasReligious || destResult.terrainType == TerrainType.PILGRIMAGE) {
            generatedItems.add(PackingItem(name = "Easy Slip-On Sandals", category = "Activity-Specific Items", quantity = "1 pair"))
            generatedItems.add(PackingItem(name = "Shawl / Dupatta for Temple", category = "Activity-Specific Items", quantity = "1"))
        }

        if (itineraryResult.hasWildlife || destResult.terrainType == TerrainType.FOREST) {
            generatedItems.add(PackingItem(name = "Insect Repellent Spray", category = "Activity-Specific Items", quantity = "1 bottle"))
            generatedItems.add(PackingItem(name = "Binoculars & Neutral Toned Clothes", category = "Activity-Specific Items", quantity = "1 set"))
        }

        if (!itineraryResult.hasTrekking && !itineraryResult.hasBeach && !itineraryResult.hasReligious && !itineraryResult.hasWildlife) {
            generatedItems.add(PackingItem(name = "Comfortable Walking Shoes", category = "Activity-Specific Items", quantity = "1 pair"))
            generatedItems.add(PackingItem(name = "Travel Neck Pillow", category = "Activity-Specific Items", quantity = "1"))
        }

        // --- CATEGORY 6: DOCUMENTS AND SAFETY ---
        val travelerText = if (trip.members > 1) "For ${trip.members} travelers" else "Original + Copy"
        generatedItems.add(PackingItem(name = "Government ID (Aadhaar/Passport/DL)", category = "Documents and Safety", quantity = travelerText))
        generatedItems.add(PackingItem(name = "Flight / Train / Bus Tickets", category = "Documents and Safety", quantity = "Digital/Print"))
        generatedItems.add(PackingItem(name = "Hotel Confirmation Voucher", category = "Documents and Safety", quantity = "1 copy"))
        generatedItems.add(PackingItem(name = "Emergency Cash & Bank Cards", category = "Documents and Safety", quantity = "1 wallet"))
        generatedItems.add(PackingItem(name = "First-Aid Kit & Personal Medicines", category = "Documents and Safety", quantity = "1 kit"))

        // --- 4. Deduplicate & Merge with Preserved Custom Items / Checked State ---
        val customItems = existingItems.filter { it.isCustom }
        val checkedNames = existingItems.filter { it.isPacked }.map { it.name.lowercase().trim() }.toSet()

        val deduplicatedList = mutableListOf<PackingItem>()
        val seenNames = mutableSetOf<String>()

        generatedItems.forEach { item ->
            val norm = item.name.lowercase().trim()
            if (!seenNames.contains(norm)) {
                seenNames.add(norm)
                val isPacked = checkedNames.contains(norm)
                deduplicatedList.add(item.copy(isPacked = isPacked))
            }
        }

        // Add user-added custom items that aren't duplicated
        customItems.forEach { custom ->
            val norm = custom.name.lowercase().trim()
            if (!seenNames.contains(norm)) {
                seenNames.add(norm)
                deduplicatedList.add(custom)
            }
        }

        return RecommendationAnalysisSummary(
            factorsSummary = analysisSummary,
            items = deduplicatedList
        )
    }

    private fun calculateDurationInDays(datesString: String): Int {
        if (datesString.isBlank()) return 3
        val parts = datesString.split("-")
        val startStr = parts.firstOrNull()?.trim() ?: ""
        val endStr = parts.getOrNull(1)?.trim() ?: startStr

        val formats = arrayOf(
            SimpleDateFormat("MMM dd, yyyy", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US),
            SimpleDateFormat("dd/MM/yyyy", Locale.US),
            SimpleDateFormat("MM/dd/yyyy", Locale.US)
        )

        var startDate: Date? = null
        var endDate: Date? = null

        for (fmt in formats) {
            if (startDate == null && startStr.isNotEmpty()) {
                try { startDate = fmt.parse(startStr) } catch (e: Exception) {}
            }
            if (endDate == null && endStr.isNotEmpty()) {
                try { endDate = fmt.parse(endStr) } catch (e: Exception) {}
            }
        }

        if (startDate != null && endDate != null) {
            val diff = endDate.time - startDate.time
            val days = (diff / (1000 * 60 * 60 * 24)).toInt() + 1
            return days.coerceAtLeast(1)
        }
        return 3
    }
}
