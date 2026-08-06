package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BentoRedAccent,
    onPrimary = Color.White,
    primaryContainer = BentoRedDark,
    onPrimaryContainer = Color.White,
    secondary = BentoGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3E2723),
    background = BentoBgDark,
    surface = BentoCardDarkSurface,
    onBackground = Color(0xFFEEEEEE),
    onSurface = Color(0xFFEEEEEE),
    surfaceVariant = Color(0xFF2A2828),
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = Color(0xFF443D3D)
)

private val LightColorScheme = lightColorScheme(
    primary = BentoRedPrimary,
    onPrimary = Color.White,
    primaryContainer = BentoRedLight,
    onPrimaryContainer = BentoRedPrimary,
    secondary = BentoGoldDark,
    onSecondary = Color.White,
    secondaryContainer = BentoGoldLight,
    background = BentoBgLight,
    surface = BentoCardSurface,
    onBackground = Color(0xFF1C1919),
    onSurface = Color(0xFF1C1919),
    surfaceVariant = Color(0xFFF3ECEC),
    onSurfaceVariant = Color(0xFF534343),
    outline = Color(0xFFE2D8D8)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded KSRTC Red/Gold theme dominant
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
