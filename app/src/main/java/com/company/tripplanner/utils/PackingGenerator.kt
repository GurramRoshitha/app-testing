package com.company.tripplanner.utils

import com.company.tripplanner.data.models.PackingItem
import com.company.tripplanner.data.models.Trip
import java.text.SimpleDateFormat
import java.util.*

object PackingGenerator {

    fun generateSuggestions(trip: Trip): List<PackingItem> {
        val items = mutableListOf<PackingItem>()
        val destLower = trip.name.lowercase().trim()

        // 1. Calculate duration in days
        val dayCount = calculateDurationInDays(trip.dates)

        // 2. Identify destination features
        val isBeach = isBeachDestination(destLower, trip)
        val isCold = isColdDestination(destLower)
        val isReligious = isReligiousDestination(destLower)
        val isTrekking = isTrekkingDestination(destLower, trip)

        // --- CATEGORY 1: CLOTHING ---
        val shirtCount = (dayCount + 1).coerceAtMost(8)
        val pantsCount = Math.max(2, (dayCount / 2) + 1)
        val undergarmentCount = (dayCount + 1).coerceAtMost(8)
        val socksCount = (dayCount + 1).coerceAtMost(7)

        items.add(PackingItem(name = "Shirts / Tops", category = "Clothing", quantity = "$shirtCount"))
        items.add(PackingItem(name = "Pants / Trousers / Shorts", category = "Clothing", quantity = "$pantsCount"))
        items.add(PackingItem(name = "Undergarments", category = "Clothing", quantity = "$undergarmentCount"))
        items.add(PackingItem(name = "Socks", category = "Clothing", quantity = "$socksCount pairs"))
        items.add(PackingItem(name = "Sleepwear / Nightwear", category = "Clothing", quantity = "${Math.max(1, dayCount / 2)} set(s)"))

        if (isCold) {
            items.add(PackingItem(name = "Warm Jacket / Sweater", category = "Clothing", quantity = "1-2"))
            items.add(PackingItem(name = "Thermal Innerwear", category = "Clothing", quantity = "1-2 sets"))
        }

        if (isBeach) {
            items.add(PackingItem(name = "Swimwear / Beachwear", category = "Clothing", quantity = "2 sets"))
            items.add(PackingItem(name = "Breathable Cotton Tops", category = "Clothing", quantity = "$dayCount"))
        }

        if (isReligious) {
            items.add(PackingItem(name = "Modest / Traditional Attire", category = "Clothing", quantity = "2-3 sets"))
        }

        // --- CATEGORY 2: TRAVEL ESSENTIALS ---
        items.add(PackingItem(name = "Backpack / Daypack", category = "Travel Essentials", quantity = "1"))
        items.add(PackingItem(name = "Mobile Phone & Charger", category = "Travel Essentials", quantity = "1"))
        items.add(PackingItem(name = "Power Bank", category = "Travel Essentials", quantity = "1"))
        items.add(PackingItem(name = "Reusable Water Bottle", category = "Travel Essentials", quantity = "1"))
        items.add(PackingItem(name = "Sunglasses", category = "Travel Essentials", quantity = "1"))
        items.add(PackingItem(name = "Compact Travel Umbrella", category = "Travel Essentials", quantity = "1"))

        // --- CATEGORY 3: TOILETRIES ---
        items.add(PackingItem(name = "Toothbrush & Toothpaste", category = "Toiletries", quantity = "1 kit"))
        items.add(PackingItem(name = "Shampoo & Shower Gel", category = "Toiletries", quantity = "1 travel kit"))
        items.add(PackingItem(name = "Face Wash & Moisturizer", category = "Toiletries", quantity = "1 set"))
        items.add(PackingItem(name = "Deodorant / Perfume", category = "Toiletries", quantity = "1"))
        items.add(PackingItem(name = "Hand Sanitizer & Wet Wipes", category = "Toiletries", quantity = "1 pack"))

        // --- CATEGORY 4: WEATHER ESSENTIALS ---
        if (isBeach || destLower.contains("summer")) {
            items.add(PackingItem(name = "Sunscreen Lotion (SPF 50+)", category = "Weather Essentials", quantity = "1 bottle"))
            items.add(PackingItem(name = "Lip Balm with SPF", category = "Weather Essentials", quantity = "1"))
            items.add(PackingItem(name = "Wide-Brim Hat / Cap", category = "Weather Essentials", quantity = "1"))
        } else if (isCold) {
            items.add(PackingItem(name = "Cold Cream & Moisturizer", category = "Weather Essentials", quantity = "1 tube"))
            items.add(PackingItem(name = "Lip Balm", category = "Weather Essentials", quantity = "1"))
            items.add(PackingItem(name = "Woolen Gloves & Cap", category = "Weather Essentials", quantity = "1 pair"))
        } else {
            items.add(PackingItem(name = "Sunscreen Lotion", category = "Weather Essentials", quantity = "1"))
            items.add(PackingItem(name = "Raincoat / Rain Poncho", category = "Weather Essentials", quantity = "1"))
        }

        // --- CATEGORY 5: ACTIVITY-SPECIFIC ITEMS ---
        if (isTrekking) {
            items.add(PackingItem(name = "Trekking Boots / Hiking Shoes", category = "Activity-Specific Items", quantity = "1 pair"))
            items.add(PackingItem(name = "Trekking Pole / Stick", category = "Activity-Specific Items", quantity = "1"))
            items.add(PackingItem(name = "Flashlight / Torch & Batteries", category = "Activity-Specific Items", quantity = "1"))
            items.add(PackingItem(name = "Energy Bars & Electrolytes", category = "Activity-Specific Items", quantity = "1 pack"))
        } else if (isBeach) {
            items.add(PackingItem(name = "Flip-Flops / Beach Sandals", category = "Activity-Specific Items", quantity = "1 pair"))
            items.add(PackingItem(name = "Waterproof Phone Case", category = "Activity-Specific Items", quantity = "1"))
            items.add(PackingItem(name = "Quick-Dry Microfiber Towel", category = "Activity-Specific Items", quantity = "1"))
        } else if (isReligious) {
            items.add(PackingItem(name = "Easy Slip-On Sandals", category = "Activity-Specific Items", quantity = "1 pair"))
            items.add(PackingItem(name = "Shawl / Dupatta for Temple Cover", category = "Activity-Specific Items", quantity = "1"))
            items.add(PackingItem(name = "Small Drawstring Shoe Bag", category = "Activity-Specific Items", quantity = "1"))
        } else {
            items.add(PackingItem(name = "Comfortable Walking Shoes", category = "Activity-Specific Items", quantity = "1 pair"))
            items.add(PackingItem(name = "Travel Neck Pillow", category = "Activity-Specific Items", quantity = "1"))
        }

        // --- CATEGORY 6: DOCUMENTS AND SAFETY ---
        val travelerText = if (trip.members > 1) "For ${trip.members} travelers" else "Original + Copy"
        items.add(PackingItem(name = "Government ID (Aadhaar/Passport/DL)", category = "Documents and Safety", quantity = travelerText))
        items.add(PackingItem(name = "Flight / Train / Bus Tickets", category = "Documents and Safety", quantity = "Digital/Print"))
        items.add(PackingItem(name = "Hotel Confirmation Voucher", category = "Documents and Safety", quantity = "1 copy"))
        items.add(PackingItem(name = "Emergency Cash & Bank Cards", category = "Documents and Safety", quantity = "1 wallet"))
        items.add(PackingItem(name = "First-Aid Kit & Personal Medicines", category = "Documents and Safety", quantity = "1 kit"))

        return items
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

    private fun isBeachDestination(dest: String, trip: Trip): Boolean {
        val keywords = listOf("goa", "kovalam", "gokarna", "andaman", "nicobar", "beach", "pondicherry", "puducherry", "vizag", "visakhapatnam", "alibaug", "digha", "daman", "diu", "lakshadweep", "maldives", "bali", "phuket", "hawaii")
        if (keywords.any { dest.contains(it) }) return true

        // Check itinerary activities
        return trip.itinerary.values.flatten().any { act ->
            act.title.lowercase().contains("beach") || act.category.lowercase().contains("beach")
        }
    }

    private fun isColdDestination(dest: String): Boolean {
        val keywords = listOf("ooty", "manali", "shimla", "dharamshala", "dalhousie", "spiti", "kasol", "munnar", "kodaikanal", "coorg", "darjeeling", "gangtok", "sikkim", "tawang", "shillong", "mussoorie", "nainital", "rishikesh", "ladakh", "leh", "kashmir", "srinagar", "gulmarg", "pahalgam")
        return keywords.any { dest.contains(it) }
    }

    private fun isReligiousDestination(dest: String): Boolean {
        val keywords = listOf("tirupati", "srisailam", "varanasi", "puri", "konark", "shirdi", "ayodhya", "mathura", "vrindavan", "kedarnath", "badrinath", "haridwar", "golden temple", "amritsar", "bodh gaya", "deoghar", "somnath", "dwarka", "rameswaram", "madurai", "kanyakumari", "ujjain")
        return keywords.any { dest.contains(it) }
    }

    private fun isTrekkingDestination(dest: String, trip: Trip): Boolean {
        val keywords = listOf("trek", "trekking", "safari", "gir", "kaziranga", "jim corbett", "ranthambore", "araku", "valley of flowers", "dandeli")
        if (keywords.any { dest.contains(it) }) return true

        return trip.itinerary.values.flatten().any { act ->
            act.title.lowercase().contains("trek") || act.title.lowercase().contains("hike")
        }
    }
}
