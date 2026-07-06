package com.egorpoprotskiy.solobase.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SoloGreenLight,
    onPrimary = OnSurfaceWhite,
    primaryContainer = BrandPrimary,
    onPrimaryContainer = OnSurfaceWhite,
    secondary = BrandAccent,
    secondaryContainer = BrandAccentContainer,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = Color(0xFF334155),
    onBackground = OnSurfaceWhite,
    onSurface = OnSurfaceWhite,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF64748B),
    error = TaskReminderOverdue,
    errorContainer = TaskReminderOverdueContainer
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimaryContainer,
    onPrimaryContainer = BrandPrimary,
    secondary = BrandAccent,
    onSecondary = Color.White,
    secondaryContainer = BrandAccentContainer,
    onSecondaryContainer = Color(0xFF064E3B),
    background = AppBackground,
    surface = AppSurface,
    surfaceVariant = AppSurfaceVariant,
    onBackground = OnSurfaceBlack,
    onSurface = OnSurfaceBlack,
    onSurfaceVariant = TextSecondary,
    outline = AppOutline,
    error = TaskReminderOverdue,
    errorContainer = TaskReminderOverdueContainer
)

@Composable
fun SoloBaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
//    val myLightScheme = lightColorScheme(
//        primary = SoloGreen,
//        background = androidx.compose.ui.graphics.Color.Yellow, // Сделаем фон желтым ТУТ
//        surface = androidx.compose.ui.graphics.Color.White
//    )
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = SoloShapes,
        content = content
    )
}
