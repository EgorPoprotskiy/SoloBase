package com.egorpoprotskiy.solobase.ui.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.graphics.Color

@Composable
fun EisenhowerMatrix(
    tasks: List<Task>,
    onTaskChecked: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Квадрант 1: Срочно и Важно
        item {
            Quadrant(
                title = "Срочно & Важно",
                tasks = tasks.filter { it.isUrgent && it.isImportant },
                color = UrgentRed,
                onTaskChecked = onTaskChecked
            )
        }
        // Квадрант 2: Важно, но не срочно
        item {
            Quadrant(
                title = "Важно",
                tasks = tasks.filter { !it.isUrgent && it.isImportant },
                color = ImportantGold,
                onTaskChecked = onTaskChecked
            )
        }
        // Квадрант 3: Срочно, но не важно
        item {
            Quadrant(
                title = "Срочно",
                tasks = tasks.filter { it.isUrgent && !it.isImportant },
                color = MaterialTheme.colorScheme.primary, // Твой SoloGreen
                onTaskChecked = onTaskChecked
            )
        }
        // Квадрант 4: Не срочно и не важно
        item {
            Quadrant(
                title = "Прочее",
                tasks = tasks.filter { !it.isUrgent && !it.isImportant },
                color = MaterialTheme.colorScheme.outline,
                onTaskChecked = onTaskChecked
            )
        }
    }
}

@Composable
fun Quadrant(
    title: String,
    tasks: List<Task>,
    color: androidx.compose.ui.graphics.Color,
    onTaskChecked: (Task, Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp), // Фиксированная высота, чтобы сетка была ровной
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f) // Очень бледный фон в цвет заголовка
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = color,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                items(tasks) { task ->
                    TaskMiniItem(
                        task = task,
                        onCheckedChange = { onTaskChecked(task, it) }
                    )
                }
//                items(10) { index ->
//                    Box(modifier = Modifier.fillMaxWidth().height(20.dp).background(if (index % 2 == 0) Color.Red else Color.Blue))
//                }
            }
        }
    }
}