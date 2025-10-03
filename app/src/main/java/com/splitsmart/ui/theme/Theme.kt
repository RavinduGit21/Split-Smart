package com.splitsmart.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

// Vibrant accent: Coral-ish
private val AccentPrimaryLight = Color(0xFFFB5A5A)
private val AccentPrimaryDark = Color(0xFFFF7A7A)

private val LightColors = lightColorScheme(
    primary = AccentPrimaryLight,
    onPrimary = Color.White,
    secondary = AccentPrimaryLight,
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onSurface = Color(0xFF111827)
)
private val DarkColors = darkColorScheme(
    primary = AccentPrimaryDark,
    onPrimary = Color(0xFF140101),
    secondary = AccentPrimaryDark,
    onSecondary = Color(0xFF140101),
    background = Color(0xFF0B0F14),
    surface = Color(0xFF121826),
    onSurface = Color(0xFFE5E7EB)
)

private val AppTypography = Typography()
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6),
    small = RoundedCornerShape(10),
    medium = RoundedCornerShape(14),
    large = RoundedCornerShape(18),
    extraLarge = RoundedCornerShape(24)
)

@Composable
fun SplitSmartTheme(
	useDarkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit
) {
	val context = LocalContext.current
	val colors = when {
		Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}
		useDarkTheme -> DarkColors
		else -> LightColors
	}
    MaterialTheme(colorScheme = colors, typography = AppTypography, shapes = AppShapes, content = content)
}

