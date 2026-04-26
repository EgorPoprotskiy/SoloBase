package com.egorpoprotskiy.solobase.ui.tasks.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.util.TableInfo
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.ui.theme.ImportantGold
import com.egorpoprotskiy.solobase.ui.theme.SoloBaseTheme
import com.egorpoprotskiy.solobase.ui.theme.UrgentRed

@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium, // Использует 16.dp из Shape.kt
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Checkbox(
//                checked = task.isCompleted,
//                onCheckedChange = onCheckedChange,
//                colors = CheckboxDefaults.colors(
//                    checkedColor = MaterialTheme.colorScheme.secondary, // Оранжевый
//                    uncheckedColor = MaterialTheme.colorScheme.outline
//                )
//            )
            IconButton(
                onClick = { onCheckedChange(!task.isCompleted) },
                modifier = Modifier.size(32.dp) // Чуть больше зона клика для комфорта
            ) {
                Icon(
                    imageVector = if (task.isCompleted)
                        Icons.Filled.CheckCircle
                    else
                        Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (task.isCompleted)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.outline
                )
            }

            Text(
                text = task.content,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                ),
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onSurface
            )

            // --- БЛОК ИКОНОК-ИНДИКАТОРОВ ---
            if (task.isUrgent) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Urgent",
                    tint = if (task.isCompleted) UrgentRed.copy(alpha = 0.4f) else UrgentRed,
                    modifier = Modifier.size(22.dp).padding(end = 4.dp)
                )
            }

            if (task.isImportant) {
                Icon(
                    imageVector = Icons.Default.Star, // ВОТ ОНА! :)
                    contentDescription = "Important",
                    tint = if (task.isCompleted) ImportantGold.copy(alpha = 0.4f) else ImportantGold,
                    modifier = Modifier.size(22.dp).padding(end = 4.dp)
                )
            }
            // ------------------------------

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ПРАВИЛЬНЫЕ ПРЕВЬЮ - теперь они не будут розовыми!
@Preview(showBackground = true)
@Composable
fun TaskItemLightPreview() {
    SoloBaseTheme(darkTheme = false) { // Используем твою тему!
        TaskItem(
            task = Task(content = "Сделать крутой дизайн SoloBase", isUrgent = true, isImportant = true, isCompleted = false, timestamp = 0, position = 0),
            onCheckedChange = {}, onClick = {}, onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemDarkPreview() {
    SoloBaseTheme(darkTheme = true) { // Проверка темной темы
        TaskItem(
            task = Task(content = "Выполненная задача", isUrgent = true, isImportant = true, isCompleted = true, timestamp = 0, position = 0),
            onCheckedChange = {}, onClick = {}, onDeleteClick = {}
        )
    }
}