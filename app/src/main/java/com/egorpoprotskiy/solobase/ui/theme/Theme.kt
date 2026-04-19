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
    primaryContainer = SoloGreen, // Для темной темы можно чуть потемнее
    onPrimaryContainer = OnSurfaceWhite,
    secondary = SoloOrange,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = OnSurfaceWhite,
    onSurface = OnSurfaceWhite
)

private val LightColorScheme = lightColorScheme(
    primary = SoloGreen,
    onPrimary = Color.White,
    primaryContainer = SoloGreen,
    onPrimaryContainer = Color.White,
    secondary = SoloOrange,
    onSecondary = Color.White, // Белая иконка на оранжевом фоне
    secondaryContainer = SoloOrange,
    onSecondaryContainer = Color.White,
    background = BackgroundLight,
    surface = Color.White,
    onBackground = OnSurfaceBlack,
    onSurface = OnSurfaceBlack
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