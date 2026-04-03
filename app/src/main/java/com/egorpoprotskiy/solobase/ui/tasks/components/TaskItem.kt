package com.egorpoprotskiy.solobase.ui.tasks.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.ui.theme.ImportantGold
import com.egorpoprotskiy.solobase.ui.theme.UrgentRed

@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    //Плавная анимация цвета текста
    val textColor by animateColorAsState(
        //Целевой цвет зависит от выполнения
        targetValue = if (task.isCompleted) {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) // Тусклый
        } else {
            MaterialTheme.colorScheme.onSurface // Обычный
        },
        // Настройка анимации: 300мс, мягкое перетекание
        animationSpec = tween(durationMillis = 300),
        label = "TextColorAnimation"
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange
            )
            Text(
                text = task.content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    // Сохраняем перечеркивание
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                ),
                color = textColor,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // Срочно (Молния)
            if (task.isUrgent) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    // Используем error цвет, но делаем тусклым, если задача выполнена
                    tint = if (task.isCompleted) {
                        UrgentRed.copy(alpha = 0.5f)
                    } else {
                        UrgentRed
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
            // Важно (Звезда)
            if (task.isImportant) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (task.isCompleted) {
                        ImportantGold.copy(alpha = 0.5f)
                    } else {
                        ImportantGold
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemLightPreview() {
    val task = Task(
        content = "Пример задачи",
        isUrgent = true,
        isImportant = true,
        timestamp = System.currentTimeMillis(),
        position = 1,
        tagId = null,
        isCompleted = false,
        projectId = null
    )
    MaterialTheme(colorScheme = lightColorScheme()) {
        TaskItem(
            task = task,
            onCheckedChange = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemDarkPreview() {
    val task = Task(
        content = "Пример задачи",
        isUrgent = true,
        isImportant = true,
        timestamp = System.currentTimeMillis(),
        position = 1,
        tagId = null,
        isCompleted = true,
        projectId = null
    )
    MaterialTheme(colorScheme = darkColorScheme()) {
        TaskItem(
            task = task,
            onCheckedChange = {},
            onClick = {}
        )
    }
}