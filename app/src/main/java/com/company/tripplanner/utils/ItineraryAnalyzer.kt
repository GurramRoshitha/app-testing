package com.company.tripplanner.utils

import com.company.tripplanner.data.models.VoyageActivity

data class ItineraryAnalysisResult(
    val hasTrekking: Boolean,
    val hasBeach: Boolean,
    val hasReligious: Boolean,
    val hasWildlife: Boolean,
    val hasCamping: Boolean,
    val hasSightseeing: Boolean,
    val summaryText: String
)

object ItineraryAnalyzer {

    fun analyze(itinerary: Map<String, List<VoyageActivity>>): ItineraryAnalysisResult {
        val allActivities = itinerary.values.flatten()

        var trekking = false
        var beach = false
        var religious = false
        var wildlife = false
        var camping = false
        var sightseeing = false

        allActivities.forEach { act ->
            val text = "${act.title} ${act.category} ${act.description}".lowercase()

            if (text.contains("trek") || text.contains("hike") || text.contains("peak") || text.contains("climb") || text.contains("trail")) {
                trekking = true
            }
            if (text.contains("beach") || text.contains("swim") || text.contains("cruise") || text.contains("island") || text.contains("scuba") || text.contains("snorkeling")) {
                beach = true
            }
            if (text.contains("temple") || text.contains("darshan") || text.contains("pooja") || text.contains("shrine") || text.contains("church") || text.contains("mosque") || text.contains("gurudwara") || text.contains("ashram")) {
                religious = true
            }
            if (text.contains("safari") || text.contains("jungle") || text.contains("wildlife") || text.contains("sanctuary") || text.contains("national park") || text.contains("zoo")) {
                wildlife = true
            }
            if (text.contains("camp") || text.contains("tent") || text.contains("bonfire") || text.contains("stargazing")) {
                camping = true
            }
            if (text.contains("sightseeing") || text.contains("fort") || text.contains("palace") || text.contains("museum") || text.contains("market") || text.contains("monument") || text.contains("viewpoint")) {
                sightseeing = true
            }
        }

        val tags = mutableListOf<String>()
        if (trekking) tags.add("Trekking/Hiking")
        if (beach) tags.add("Beach Visit")
        if (religious) tags.add("Religious Visit")
        if (wildlife) tags.add("Wildlife Safari")
        if (camping) tags.add("Camping")
        if (sightseeing || tags.isEmpty()) tags.add("City Sightseeing")

        val summary = "Planned Activities: ${tags.joinToString(", ")}"

        return ItineraryAnalysisResult(
            hasTrekking = trekking,
            hasBeach = beach,
            hasReligious = religious,
            hasWildlife = wildlife,
            hasCamping = camping,
            hasSightseeing = sightseeing,
            summaryText = summary
        )
    }
}
