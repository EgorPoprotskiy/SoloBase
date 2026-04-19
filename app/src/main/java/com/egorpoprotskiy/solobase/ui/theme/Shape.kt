package com.egorpoprotskiy.solobase.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val SoloShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(topStart = 20.dp, bottomEnd = 20.dp, topEnd = 4.dp, bottomStart = 4.dp), // Для карточек задач
//    medium = RoundedCornerShape(topStart = 10.dp, bottomEnd = 10.dp, topEnd = 10.dp, bottomStart = 10.dp), // Для карточек задач
//    medium = RoundedCornerShape(16.dp), // Для карточек задач
    large = RoundedCornerShape(28.dp),   // Для кнопок и диалогов
    extraLarge = RoundedCornerShape(32.dp)
)