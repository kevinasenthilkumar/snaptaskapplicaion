package com.example.snaptask.ui.components

import androidx.annotation.DrawableRes
import com.example.snaptask.R
import java.util.Locale

val ALL_CATEGORIES = listOf(
    "Tickets",
    "Travel",
    "Bills & Payments",
    "Receipts & Orders",
    "Shopping",
    "Food & Recipes",
    "Study & Notes",
    "Chats & Social",
    "Maps & Places",
    "Other"
)

@DrawableRes
fun getCategoryIcon(category: String): Int {
    return when (category.lowercase(Locale.getDefault()).trim()) {
        "tickets" -> R.drawable.ic_cat_tickets
        "travel" -> R.drawable.ic_cat_travel
        "bills & payments", "bills" -> R.drawable.ic_cat_bills
        "receipts & orders", "receipts" -> R.drawable.ic_cat_receipts
        "shopping" -> R.drawable.ic_cat_shopping
        "food & recipes", "food" -> R.drawable.ic_cat_food
        "study & notes", "study" -> R.drawable.ic_cat_study
        "chats & social", "chats" -> R.drawable.ic_cat_chats
        "maps & places", "maps" -> R.drawable.ic_cat_maps
        else -> R.drawable.ic_cat_other
    }
}

fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> String.format(Locale.getDefault(), "%.1f GB", gb)
        mb >= 1.0 -> String.format(Locale.getDefault(), "%.1f MB", mb)
        kb >= 1.0 -> String.format(Locale.getDefault(), "%.1f KB", kb)
        else -> "$bytes B"
    }
}
