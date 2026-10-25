package com.company.tripplanner.utils

import java.text.SimpleDateFormat
import java.util.*

enum class SeasonType(val label: String) {
    WINTER("Winter Season (Cold / Pleasant)"),
    SUMMER("Summer Season (Warm / Hot)"),
    MONSOON("Monsoon Season (Rainy / Humid)"),
    MODERATE("Moderate Climate")
}

data class SeasonalAnalysisResult(
    val seasonType: SeasonType,
    val description: String
)

object SeasonalAnalyzer {

    fun analyze(datesString: String, terrain: TerrainType): SeasonalAnalysisResult {
        val month = parseMonthFromDates(datesString)

        val season = when (month) {
            11, 12, 1, 2 -> SeasonType.WINTER
            3, 4, 5, 6 -> SeasonType.SUMMER
            7, 8, 9, 10 -> SeasonType.MONSOON
            else -> SeasonType.MODERATE
        }

        val desc = when {
            season == SeasonType.WINTER && terrain == TerrainType.MOUNTAIN -> "Heavy Cold / Possible Frost & Chilly Winds"
            season == SeasonType.WINTER -> "Pleasant Days with Mild to Cool Nights"
            season == SeasonType.SUMMER && terrain == TerrainType.MOUNTAIN -> "Pleasant Hill Climate with Cool Breeze"
            season == SeasonType.SUMMER && terrain == TerrainType.DESERT -> "Extreme Heat during Days; Cool Nights"
            season == SeasonType.SUMMER -> "Hot & Dry / High Sun Exposure"
            season == SeasonType.MONSOON && (terrain == TerrainType.COASTAL || terrain == TerrainType.MOUNTAIN) -> "Frequent Rain Showers & High Humidity"
            season == SeasonType.MONSOON -> "Moderate to Heavy Rainfall"
            else -> "Favorable Travel Climate"
        }

        return SeasonalAnalysisResult(
            seasonType = season,
            description = desc
        )
    }

    private fun parseMonthFromDates(datesString: String): Int {
        if (datesString.isBlank()) return Calendar.getInstance().get(Calendar.MONTH) + 1
        val startDateStr = datesString.split("-").firstOrNull()?.trim() ?: datesString.trim()

        val formats = arrayOf(
            SimpleDateFormat("MMM dd, yyyy", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US),
            SimpleDateFormat("dd/MM/yyyy", Locale.US),
            SimpleDateFormat("MM/dd/yyyy", Locale.US),
            SimpleDateFormat("MMM d, yyyy", Locale.US)
        )

        for (fmt in formats) {
            try {
                val date = fmt.parse(startDateStr)
                if (date != null) {
                    val cal = Calendar.getInstance()
                    cal.time = date
                    return cal.get(Calendar.MONTH) + 1
                }
            } catch (e: Exception) {}
        }

        return Calendar.getInstance().get(Calendar.MONTH) + 1
    }
}
