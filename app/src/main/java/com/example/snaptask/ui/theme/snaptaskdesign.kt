package com.example.snaptask.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.snaptask.R

object SnapTaskColors {
    val Background = Color(0xFFFAF6F0)
    val Burgundy = Color(0xFF721D38)
    val TextPrimary = Color(0xFF2B2025)
    val TextSecondary = Color(0xFF796B70)
    val Surface = Color.White
}

val InterFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

object SnapTaskType {
    val AppBarTitle = TextStyle(
        fontFamily = InterFamily, fontWeight = FontWeight.Normal,
        fontSize = 18.sp, letterSpacing = 0.sp,
    )
    val Eyebrow = TextStyle(
        fontFamily = InterFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, letterSpacing = 0.sp,
    )
    val BadgeLabel = TextStyle(
        fontFamily = InterFamily, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, letterSpacing = 0.sp,
    )
    val Headline = TextStyle(
        fontFamily = InterFamily, fontWeight = FontWeight.Normal,
        fontSize = 40.sp, lineHeight = 47.2.sp, letterSpacing = 0.sp,
    )
    val Body = TextStyle(
        fontFamily = InterFamily, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.sp,
    )
    val Caption = TextStyle(
        fontFamily = InterFamily, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 18.sp, letterSpacing = 0.sp,
    )
    val ButtonLabel = TextStyle(
        fontFamily = InterFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp, letterSpacing = 0.sp,
    )
}