package com.egorpoprotskiy.solobase.ui.tasks.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.ui.theme.SoloBaseTheme

@Composable
fun TaskMiniItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(0.75f), // Мини-версия
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.secondary,
                uncheckedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )
        Text(
            text = task.content,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            color = if (task.isCompleted)
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true, name = "Mini Task States")
@Composable
fun MiniTaskPreview() {
    SoloBaseTheme {
        Column(modifier = Modifier.padding(8.dp).width(180.dp)) {
            TaskMiniItem(
                task = Task(content = "Купить кофе", isImportant = true, timestamp = 0, position = 0),
                onCheckedChange = {}
            )
            TaskMiniItem(
                task = Task(content = "Позвонить маме", isCompleted = true, timestamp = 0, position = 0),
                onCheckedChange = {}
            )
            TaskMiniItem(
                task = Task(content = "Очень длинная задача, которая не влезет", timestamp = 0, position = 0),
                onCheckedChange = {}
            )
        }
    }
}