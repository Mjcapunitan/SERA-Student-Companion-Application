package com.example.sera.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.sera.R

val Cabin = FontFamily(
    Font(R.font.cabin_regular),
    Font(R.font.cabin_italic, style = FontStyle.Italic),
    Font(R.font.cabin_medium, FontWeight.Medium),
    Font(R.font.cabin_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.cabin_semi_bold, FontWeight.SemiBold),
    Font(R.font.cabin_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.cabin_bold, FontWeight.Bold),
    Font(R.font.cabin_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

val typography = with(Typography()) {
    Typography(
        displayLarge = displayLarge.copy(fontFamily = Cabin),
        displayMedium = displayMedium.copy(fontFamily = Cabin),
        displaySmall = displaySmall.copy(fontFamily = Cabin),
        headlineLarge = headlineLarge.copy(fontFamily = Cabin),
        headlineMedium = headlineMedium.copy(fontFamily = Cabin),
        headlineSmall = headlineSmall.copy(fontFamily = Cabin),
        titleLarge = titleLarge.copy(fontFamily = Cabin),
        titleMedium = titleMedium.copy(fontFamily = Cabin),
        titleSmall = titleSmall.copy(fontFamily = Cabin),
        bodyLarge = bodyLarge.copy(fontFamily = Cabin),
        bodyMedium = bodyMedium.copy(fontFamily = Cabin),
        bodySmall = bodySmall.copy(fontFamily = Cabin),
        labelLarge = labelLarge.copy(fontFamily = Cabin),
        labelMedium = labelMedium.copy(fontFamily = Cabin),
        labelSmall = labelSmall.copy(fontFamily = Cabin),
    )
}

class SeraTypography(
    val itemListDetails: TextStyle = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 10.sp,
        fontFamily = Cabin,
    ),
)
