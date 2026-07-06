package com.egorpoprotskiy.solobase.ui.theme

import androidx.compose.ui.graphics.Color

// --- SOLOBASE BRAND IDENTITY ---
val BrandPrimary = Color(0xFF1F2937)
val BrandPrimaryContainer = Color(0xFFD9E2EC)
val BrandAccent = Color(0xFF10B981)
val BrandAccentContainer = Color(0xFFD1FAE5)

// --- НЕЙТРАЛЬНЫЕ ЦВЕТА ---
val AppBackground = Color(0xFFF8FAFC)
val AppSurface = Color(0xFFFFFFFF)
val AppSurfaceVariant = Color(0xFFE8EEF5)
val AppOutline = Color(0xFFCBD5E1)
val TextPrimary = Color(0xFF111827)
val TextSecondary = Color(0xFF6B7280)
val BackgroundDark = Color(0xFF121212)    // Темный фон
val SurfaceDark = Color(0xFF1E1E1E)       // Карточки в темной теме
val OnSurfaceWhite = Color(0xFFF5F5F5)
val OnSurfaceBlack = TextPrimary

// --- ДОПОЛНИТЕЛЬНЫЕ (Эйзенхауэр) ---
val TaskUrgent = Color(0xFFE11D48)
val TaskImportant = Color(0xFFF59E0B)
val TaskReminderOverdue = Color(0xFFE11D48)
val TaskReminderOverdueContainer = Color(0xFFFFE4E6)
val ProjectColorFallback = Color(0xFF64748B)

// Backward-compatible aliases for existing components.
val SoloGreen = BrandPrimary
val SoloGreenLight = Color(0xFF475569)
val SoloOrange = BrandAccent
val BackgroundLight = AppBackground
val UrgentRed = TaskUrgent
val ImportantGold = TaskImportant

// --- ЦВЕТА ТЕГОВ (Tag Colors) ---
// Пригодятся на 2-м экране для заметок и фильтров
val TagGreen = Color(0xFF4CAF50)
val TagPurple = Color(0xFF9C27B0)
val TagOrange = Color(0xFFFF9800)
val TagCyan = Color(0xFF00BCD4)
