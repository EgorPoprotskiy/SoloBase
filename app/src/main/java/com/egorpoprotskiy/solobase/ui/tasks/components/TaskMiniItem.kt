package com.egorpoprotskiy.solobase.ui.tasks.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .height(28.dp) // Вот она, та самая высота как в тесте (чуть больше для удобства)
            .clickable { onCheckedChange(!task.isCompleted) }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Используем иконку вместо чекбокса, чтобы избежать лишних отступов
        Icon(
            imageVector = if (task.isCompleted)
                Icons.Filled.CheckCircle
            else
                Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            modifier = Modifier.size(16.dp), // Маленькая аккуратная иконка
            tint = if (task.isCompleted)
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.outline
        )

        Text(
            text = task.content,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false) // Убираем отступы шрифта
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            color = if (task.isCompleted)
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 6.dp)
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