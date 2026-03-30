package com.egorpoprotskiy.solobase.ui.theme

import androidx.compose.ui.graphics.Color

// --- ОСНОВНЫЕ АКЦЕНТЫ (Brand Colors) ---
val PrimaryBlue = Color(0xFF3F8CFF)       // Яркий синий для кнопок и фокуса
val PrimaryBlueLight = Color(0xFF005AC1)  // Насыщенный синий для светлой темы
val SecondaryGray = Color(0xFF7E8494)     // Вспомогательный текст (подзаголовки)

// --- ТЕМНАЯ ТЕМА (Dark Palette) ---
val BackgroundDark = Color(0xFF121212)    // Глубокий серый (почти черный)
val SurfaceDark = Color(0xFF1E1E1E)       // Цвет карточек и панелей
val OnSurfaceWhite = Color(0xFFF5F5F5)    // Основной текст в темной теме
val OutlineDark = Color(0xFF44474E)       // Разделители и границы

// --- СВЕТЛАЯ ТЕМА (Light Palette) ---
val BackgroundLight = Color(0xFFF8F9FA)   // Светлый "дымчатый" фон (как в Google)
val SurfaceLight = Color(0xFFFFFFFF)      // Белые карточки
val OnSurfaceBlack = Color(0xFF1A1C1E)    // Глубокий черный текст
val OutlineLight = Color(0xFFC4C6D0)      // Разделители в светлой теме

// --- МАТРИЦА ЭЙЗЕНХАУЭРА (Eisenhower Accents) ---
// Мы используем их для иконок молнии и звезды
val UrgentRed = Color(0xFFFF5252)         // Срочно (Молния)
val ImportantGold = Color(0xFFFFC107)     // Важно (Звезда)

// --- ЦВЕТА ТЕГОВ (Tag Colors) ---
// Пригодятся на 2-м экране для заметок и фильтров
val TagGreen = Color(0xFF4CAF50)
val TagPurple = Color(0xFF9C27B0)
val TagOrange = Color(0xFFFF9800)
val TagCyan = Color(0xFF00BCD4)