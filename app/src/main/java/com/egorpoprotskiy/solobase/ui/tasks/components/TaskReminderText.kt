package com.egorpoprotskiy.solobase.ui.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val reminderFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")

fun formatReminderAt(reminderAt: Long): String {
    return Instant.ofEpochMilli(reminderAt)
        .atZone(ZoneId.systemDefault())
        .format(reminderFormatter)
}

@Composable
fun TaskReminderText(
    reminderAt: Long?,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    if (reminderAt == null) return

    val isOverdue = reminderAt < System.currentTimeMillis() && !isCompleted
    val contentColor = when {
        isCompleted -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
        isOverdue -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val backgroundColor = if (isOverdue) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    }

    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(percent = 50))
            .padding(horizontal = if (compact) 4.dp else 6.dp, vertical = if (compact) 1.dp else 2.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(if (compact) 10.dp else 14.dp)
        )
        Text(
            text = formatReminderAt(reminderAt),
            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
