package com.example.snaptask.data.ai

import java.util.regex.Pattern

data class ClassificationResult(
    val category: String, // Tickets, Travel, Bills & Payments, Receipts & Orders, Shopping, Food & Recipes, Study & Notes, Chats & Social, Maps & Places, Other
    val confidence: Float = 0.85f,
    val title: String? = null,
)

object LocalClassifier {

    private val CATEGORIES = mapOf(
        "Tickets" to listOf("ticket", "tickets", "admit one", "admit", "seat", "row", "bookmyshow", "pnr", "coach", "cinema", "movie", "pvr", "inox", "screen", "concert", "match"),
        "Travel" to listOf("boarding pass", "flight", "check-in", "booking id", "irctc", "makemytrip", "airline", "indigo", "terminal", "hotel", "itinerary", "train", "trip"),
        "Bills & Payments" to listOf("amount due", "paid", "upi", "transaction id", "gpay", "phonepe", "paytm", "bhim", "bank", "debited", "credited", "bill", "due date", "statement"),
        "Receipts & Orders" to listOf("invoice", "order id", "subtotal", "gst", "delivered", "tax invoice", "tracking", "courier", "delivery", "amazon order", "receipt"),
        "Shopping" to listOf("add to cart", "% off", "₹", "free delivery", "amazon", "myntra", "flipkart", "ajio", "meesho", "in stock", "price", "buy now", "size", "wishlist", "sale"),
        "Food & Recipes" to listOf("ingredients", "tbsp", "tsp", "serves", "menu", "zomato", "swiggy", "recipe", "cuisine", "restaurant", "cook time", "simmer", "bake"),
        "Study & Notes" to listOf("chapter", "assignment", "definition", "syllabus", "unit", "lecture", "slide", "course", "exam", "questions", "formula", "theorem", "code", "notes"),
        "Chats & Social" to listOf("typing…", "typing...", "seen", "likes", "followers", "following", "reply", "whatsapp", "instagram", "chat", "message", "dms", "meme"),
        "Maps & Places" to listOf("directions", "min", "km", "google maps", "open now", "navigate", "location", "map", "traffic", "distance", "address")
    )

    fun classify(text: String, fallbackFileName: String): ClassificationResult {
        val lower = text.lowercase()
        var bestCategory = "Other"
        var bestScore = 0

        for ((category, keywords) in CATEGORIES) {
            var score = 0
            for (kw in keywords) {
                if (lower.contains(kw)) {
                    score += 1
                }
            }
            if (score > bestScore) {
                bestScore = score
                bestCategory = category
            }
        }

        val title = extractTitle(text) ?: fallbackFileName
        val confidence = if (bestScore > 0) (0.65f + (bestScore * 0.08f)).coerceAtMost(0.99f) else 0.50f

        return ClassificationResult(
            category = bestCategory,
            confidence = confidence,
            title = title,
        )
    }

    private fun extractTitle(text: String): String? {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() && it.length > 3 }
        return lines.firstOrNull { line ->
            !line.contains("LTE", ignoreCase = true) &&
            !line.contains("100%", ignoreCase = true) &&
            !line.contains("AM", ignoreCase = true) &&
            !line.contains("PM", ignoreCase = true) &&
            line.length in 5..60
        }
    }
}