package com.example.sera.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.sera.common.Sera.subjectColorsDark
import com.example.sera.common.Sera.subjectColorsLight

class SeraColors(

    val red: Color,
    val green: Color,

    val tabLayoutColor: Color,
    val appBarLayoutColor: Color,
    val subjectColors: List<Color>,
)

val SeraLightColors = SeraColors(
    red = Color(0xfff44336),
    green = Color(0xFF286c2a),
    tabLayoutColor = Color(0xffffffff),
    appBarLayoutColor = Color(0xffffffff),
    subjectColors = subjectColorsLight,
)

val SeraDarkColors = SeraColors(

    red = Color(0xffef5350),
    green = Color(0xFF90d889),
    tabLayoutColor = Color(0xff272727),
    appBarLayoutColor = Color(0xff272727),
    subjectColors = subjectColorsDark,
)

val md_theme_light_primary = Color(0xFF006493)
val md_theme_light_onPrimary = Color(0xFFffffff)
val md_theme_light_primaryContainer = Color(0xFFcae6ff)
val md_theme_light_onPrimaryContainer = Color(0xFF001e30)
val md_theme_light_secondary = Color(0xFF50606e)
val md_theme_light_onSecondary = Color(0xFFffffff)
val md_theme_light_secondaryContainer = Color(0xFFd3e5f6)
val md_theme_light_onSecondaryContainer = Color(0xFF0c1d29)
val md_theme_light_tertiary = Color(0xFF65587b)
val md_theme_light_onTertiary = Color(0xFFffffff)
val md_theme_light_tertiaryContainer = Color(0xFFebdcff)
val md_theme_light_onTertiaryContainer = Color(0xFF201634)
val md_theme_light_error = Color(0xFFba1a1a)
val md_theme_light_onError = Color(0xFFffffff)
val md_theme_light_errorContainer = Color(0xFFffdad6)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFfcfcff)
val md_theme_light_onBackground = Color(0xFF1a1c1e)
val md_theme_light_surface = Color(0xFFfcfcff)
val md_theme_light_onSurface = Color(0xFF1a1c1e)
val md_theme_light_surfaceVariant = Color(0xFFdde3ea)
val md_theme_light_onSurfaceVariant = Color(0xFF72787e)
val md_theme_light_outline = Color(0xFF41474d)

val md_theme_dark_primary = Color(0xFF8dcdff)
val md_theme_dark_onPrimary = Color(0xFF00344f)
val md_theme_dark_primaryContainer = Color(0xFF004b70)
val md_theme_dark_onPrimaryContainer = Color(0xFFcae6ff)
val md_theme_dark_secondary = Color(0xFFb7c9d9)
val md_theme_dark_onSecondary = Color(0xFF22323f)
val md_theme_dark_secondaryContainer = Color(0xFF384956)
val md_theme_dark_onSecondaryContainer = Color(0xFFd3e5f6)
val md_theme_dark_tertiary = Color(0xFFcfc0e8)
val md_theme_dark_onTertiary = Color(0xFF362b4a)
val md_theme_dark_tertiaryContainer = Color(0xFF4d4162)
val md_theme_dark_onTertiaryContainer = Color(0xFFebdcff)
val md_theme_dark_error = Color(0xFFffb4ab)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000a)
val md_theme_dark_onErrorContainer = Color(0xFFffdad6)
val md_theme_dark_background = Color(0xFF1a1c1e)
val md_theme_dark_onBackground = Color(0xFFe2e2e5)
val md_theme_dark_surface = Color(0xFF1a1c1e)
val md_theme_dark_onSurface = Color(0xFFe2e2e5)
val md_theme_dark_surfaceVariant = Color(0xFF41474d)
val md_theme_dark_onSurfaceVariant = Color(0xFFc1c7ce)
val md_theme_dark_outline = Color(0xFF8b9198)