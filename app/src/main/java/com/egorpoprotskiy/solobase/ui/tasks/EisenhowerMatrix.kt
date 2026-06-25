package com.egorpoprotskiy.solobase.ui.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.ui.tasks.components.TaskMiniItem
import com.egorpoprotskiy.solobase.ui.theme.ImportantGold
import com.egorpoprotskiy.solobase.ui.theme.UrgentRed
import androidx.compose.material3.Text

@Composable
fun EisenhowerMatrix(
    tasks: List<Task>,
    onTaskChecked: (Task, Boolean) -> Unit,
    onTaskLongClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Quadrant(
                title = "Срочно & Важно",
                tasks = tasks.filter { it.isUrgent && it.isImportant },
                color = UrgentRed,
                onTaskChecked = onTaskChecked,
                onTaskLongClick = onTaskLongClick,
                modifier = Modifier.weight(1f)
            )
            Quadrant(
                title = "Важно",
                tasks = tasks.filter { !it.isUrgent && it.isImportant },
                color = ImportantGold,
                onTaskChecked = onTaskChecked,
                onTaskLongClick = onTaskLongClick,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Quadrant(
                title = "Срочно",
                tasks = tasks.filter { it.isUrgent && !it.isImportant },
                color = MaterialTheme.colorScheme.primary, // Твой SoloGreen
                onTaskChecked = onTaskChecked,
                onTaskLongClick = onTaskLongClick,
                modifier = Modifier.weight(1f)
            )
            Quadrant(
                title = "Прочее",
                tasks = tasks.filter { !it.isUrgent && !it.isImportant },
                color = MaterialTheme.colorScheme.outline,
                onTaskChecked = onTaskChecked,
                onTaskLongClick = onTaskLongClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun Quadrant(
    title: String,
    tasks: List<Task>,
    color: androidx.compose.ui.graphics.Color,
    onTaskChecked: (Task, Boolean) -> Unit,
    onTaskLongClick: (Task) -> Unit, //колбэк на долгое нажатие для матрицы
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f) // Очень бледный фон в цвет заголовка
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = color,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                items(tasks) { task ->
                    TaskMiniItem(
                        task = task,
                        onCheckedChange = { onTaskChecked(task, it) },
                        onLongClick = {onTaskLongClick(task)} // Передаем задачу в колбек
                    )
                }
//                items(10) { index ->
//                    Box(modifier = Modifier.fillMaxWidth().height(20.dp).background(if (index % 2 == 0) Color.Red else Color.Blue))
//                }
            }
        }
    }
}
