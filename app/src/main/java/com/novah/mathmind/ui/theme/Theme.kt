package com.novah.mathmind.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Neubrutalism color palette – vibrant, flat, bold.
 */
object NeuColors {
    val Yellow = Color(0xFFFFE156)
    val Pink = Color(0xFFFF6B8A)
    val Blue = Color(0xFF6FB4FF)
    val Green = Color(0xFF77DD77)
    val Orange = Color(0xFFFFB347)
    val Purple = Color(0xFFCB97FF)
    val White = Color(0xFFFFFDF0)
    val Black = Color(0xFF1A1A1A)
    val LightGray = Color(0xFFF5F0E1)
    val DarkBg = Color(0xFF1E1E2E)
    val DarkSurface = Color(0xFF2A2A3C)
}

// Neubrutalism light color scheme
private val NeuLightColorScheme = lightColorScheme(
    primary = Color(0xFF1A1A1A),
    onPrimary = Color(0xFFFFFDF0),
    primaryContainer = NeuColors.Yellow,
    onPrimaryContainer = Color(0xFF1A1A1A),
    secondary = NeuColors.Pink,
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = NeuColors.Blue,
    onSecondaryContainer = Color(0xFF1A1A1A),
    tertiary = NeuColors.Green,
    onTertiary = Color(0xFF1A1A1A),
    background = NeuColors.White,
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFDF0),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = NeuColors.LightGray,
    onSurfaceVariant = Color(0xFF1A1A1A),
    error = Color(0xFFFF4444),
    onError = Color.White
)

// Neubrutalism dark color scheme
private val NeuDarkColorScheme = darkColorScheme(
    primary = NeuColors.Yellow,
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = Color(0xFF3A3A4C),
    onPrimaryContainer = NeuColors.Yellow,
    secondary = NeuColors.Pink,
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = Color(0xFF4A2A3C),
    onSecondaryContainer = NeuColors.Pink,
    tertiary = NeuColors.Green,
    onTertiary = Color(0xFF1A1A1A),
    background = NeuColors.DarkBg,
    onBackground = Color(0xFFFFFDF0),
    surface = NeuColors.DarkSurface,
    onSurface = Color(0xFFFFFDF0),
    surfaceVariant = Color(0xFF3A3A4C),
    onSurfaceVariant = Color(0xFFCCCCCC),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF1A1A1A)
)

// Bold neubrutalism typography
private val NeuTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        letterSpacing = (-0.3).sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
)

/**
 * NovahMathMind theme with Neubrutalism design.
 * Bold borders, solid shadows, vibrant flat colors, strong typography.
 */
@Composable
fun NovahMathMindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NeuDarkColorScheme else NeuLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NeuTypography,
        content = content
    )
}
