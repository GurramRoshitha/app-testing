package com.company.tripplanner.utils

enum class TerrainType(val label: String) {
    COASTAL("Coastal & Beach Environment"),
    MOUNTAIN("Mountainous & High Altitude"),
    DESERT("Desert & Arid Region"),
    FOREST("Forest & Wildlife Sanctuary"),
    PILGRIMAGE("Cultural & Pilgrimage Destination"),
    URBAN("Urban & City Environment")
}

data class DestinationAnalysisResult(
    val destinationName: String,
    val terrainType: TerrainType,
    val summaryText: String
)

object DestinationAnalyzer {

    fun analyze(destinationName: String): DestinationAnalysisResult {
        val dest = destinationName.lowercase().trim()

        val terrain = when {
            isCoastal(dest) -> TerrainType.COASTAL
            isMountainous(dest) -> TerrainType.MOUNTAIN
            isDesert(dest) -> TerrainType.DESERT
            isForest(dest) -> TerrainType.FOREST
            isPilgrimage(dest) -> TerrainType.PILGRIMAGE
            else -> TerrainType.URBAN
        }

        return DestinationAnalysisResult(
            destinationName = destinationName,
            terrainType = terrain,
            summaryText = terrain.label
        )
    }

    private fun isCoastal(dest: String): Boolean {
        val keywords = listOf(
            "goa", "kovalam", "gokarna", "andaman", "nicobar", "beach", "pondicherry", "puducherry",
            "vizag", "visakhapatnam", "alibaug", "digha", "daman", "diu", "lakshadweep", "maldives",
            "bali", "phuket", "hawaii", "varkala", "puri", "kanyakumari", "mahabalipuram", "chennai",
            "mumbai", "udupi", "mangalore", "dhanushkodi", "kashid", "tarkarli", "ganpatipule", "mandi"
        )
        return keywords.any { dest.contains(it) }
    }

    private fun isMountainous(dest: String): Boolean {
        val keywords = listOf(
            "ooty", "manali", "shimla", "dharamshala", "dalhousie", "spiti", "kasol", "munnar",
            "kodaikanal", "coorg", "darjeeling", "gangtok", "sikkim", "tawang", "shillong", "mussoorie",
            "nainital", "rishikesh", "ladakh", "leh", "kashmir", "srinagar", "gulmarg", "pahalgam",
            "chamba", "lonavala", "mahabaleshwar", "horsley hills", "araku", "chikmagalur", "wayanad",
            "mount abu", "pelling", "lachung", "hills", "valley", "peak", "heights", "alps"
        )
        return keywords.any { dest.contains(it) }
    }

    private fun isDesert(dest: String): Boolean {
        val keywords = listOf(
            "jaisalmer", "jodhpur", "bikaner", "kutch", "thar", "rann of kutch", "desert", "pushkar",
            "barmer", "dunes"
        )
        return keywords.any { dest.contains(it) }
    }

    private fun isForest(dest: String): Boolean {
        val keywords = listOf(
            "jim corbett", "kaziranga", "gir", "ranthambore", "sundarbans", "bandhavgarh", "kanha",
            "periyar", "thekkady", "wayanad", "mudumalai", "bandipur", "kabini", "nagarhole",
            "dandeli", "forest", "sanctuary", "national park", "wildlife", "safari"
        )
        return keywords.any { dest.contains(it) }
    }

    private fun isPilgrimage(dest: String): Boolean {
        val keywords = listOf(
            "tirupati", "srisailam", "varanasi", "puri", "shirdi", "ayodhya", "mathura", "vrindavan",
            "kedarnath", "badrinath", "haridwar", "golden temple", "amritsar", "bodh gaya", "deoghar",
            "somnath", "dwarka", "rameswaram", "madurai", "kanyakumari", "ujjain", "trimbakeshwar",
            "pawapuri", "nalanda", "rajgir", "katra", "vaishno devi", "palani", "sabari", "temple", "shrine"
        )
        return keywords.any { dest.contains(it) }
    }
}
