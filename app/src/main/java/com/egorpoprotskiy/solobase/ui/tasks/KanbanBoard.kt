package com.egorpoprotskiy.solobase.ui.tasks

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.annotation.StringRes
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.egorpoprotskiy.solobase.R
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.models.TaskStatus
import com.egorpoprotskiy.solobase.ui.tasks.components.TaskReminderText
import com.egorpoprotskiy.solobase.ui.theme.TaskImportant
import com.egorpoprotskiy.solobase.ui.theme.TaskUrgent

@Composable
fun KanbanBoard(
    tasks: List<Task>,
    onMoveTask: (Task, TaskStatus) -> Unit,
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val columnBounds = remember { mutableStateMapOf<TaskStatus, Rect>() }
    var draggedTask by remember { mutableStateOf<Task?>(null) }
    var dragPosition by remember { mutableStateOf<Offset?>(null) }

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        kanbanColumns.forEach { column ->
            KanbanColumn(
                column = column,
                tasks = tasks
                    .filter { it.status == column.status.name }
                    .sortedWith(compareBy<Task> { it.position }.thenByDescending { it.timestamp }),
                onMoveTask = onMoveTask,
                onTaskClick = onTaskClick,
                onColumnPositioned = { bounds ->
                    columnBounds[column.status] = bounds
                },
                draggedTask = draggedTask,
                onDragStarted = { task, position ->
                    draggedTask = task
                    dragPosition = position
                },
                onDragMoved = { position ->
                    dragPosition = position
                },
                onDragEnded = {
                    val task = draggedTask
                    val position = dragPosition
                    if (task != null && position != null) {
                        columnBounds.entries
                            .firstOrNull { (_, bounds) -> bounds.contains(position) }
                            ?.key
                            ?.takeIf { it.name != task.status }
                            ?.let { status -> onMoveTask(task, status) }
                    }
                    draggedTask = null
                    dragPosition = null
                }
            )
        }
    }
}

@Composable
private fun KanbanColumn(
    column: KanbanColumnInfo,
    tasks: List<Task>,
    onMoveTask: (Task, TaskStatus) -> Unit,
    onTaskClick: (Task) -> Unit,
    onColumnPositioned: (Rect) -> Unit,
    draggedTask: Task?,
    onDragStarted: (Task, Offset) -> Unit,
    onDragMoved: (Offset) -> Unit,
    onDragEnded: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .onGloballyPositioned { coordinates ->
                val position = coordinates.positionInRoot()
                onColumnPositioned(
                    Rect(
                        offset = position,
                        size = coordinates.size.toSize()
                    )
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(
                    R.string.kanban_column_title,
                    stringResource(column.titleRes),
                    tasks.size
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = tasks,
                    key = { it.id }
                ) { task ->
                    KanbanTaskCard(
                        task = task,
                        status = column.status,
                        onMoveTask = onMoveTask,
                        onTaskClick = onTaskClick,
                        isDragging = draggedTask?.id == task.id,
                        onDragStarted = onDragStarted,
                        onDragMoved = onDragMoved,
                        onDragEnded = onDragEnded
                    )
                }
            }
        }
    }
}

@Composable
private fun KanbanTaskCard(
    task: Task,
    status: TaskStatus,
    onMoveTask: (Task, TaskStatus) -> Unit,
    onTaskClick: (Task) -> Unit,
    isDragging: Boolean,
    onDragStarted: (Task, Offset) -> Unit,
    onDragMoved: (Offset) -> Unit,
    onDragEnded: () -> Unit
) {
    var cardPosition by remember { mutableStateOf(Offset.Zero) }
    var pointerPosition by remember { mutableStateOf(Offset.Zero) }

    Card(
        onClick = { onTaskClick(task) },
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                cardPosition = coordinates.positionInRoot()
            }
            .pointerInput(task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        pointerPosition = cardPosition + offset
                        onDragStarted(task, pointerPosition)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        pointerPosition += dragAmount
                        onDragMoved(pointerPosition)
                    },
                    onDragEnd = onDragEnded,
                    onDragCancel = onDragEnded
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = task.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            TaskReminderText(
                reminderAt = task.reminderAt,
                isCompleted = task.isCompleted
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (task.isUrgent) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = TaskUrgent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (task.isImportant) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = TaskImportant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Row {
                    IconButton(
                        onClick = { previousStatus(status)?.let { onMoveTask(task, it) } },
                        enabled = previousStatus(status) != null,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_move_left),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { nextStatus(status)?.let { onMoveTask(task, it) } },
                        enabled = nextStatus(status) != null,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(R.string.content_description_move_right),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class KanbanColumnInfo(
    val status: TaskStatus,
    @param:StringRes val titleRes: Int
)

private val kanbanColumns = listOf(
    KanbanColumnInfo(TaskStatus.BACKLOG, R.string.kanban_backlog),
    KanbanColumnInfo(TaskStatus.TODO, R.string.kanban_todo),
    KanbanColumnInfo(TaskStatus.IN_PROGRESS, R.string.kanban_in_progress),
    KanbanColumnInfo(TaskStatus.DONE, R.string.kanban_done)
)

private fun previousStatus(status: TaskStatus): TaskStatus? {
    val index = kanbanColumns.indexOfFirst { it.status == status }
    return kanbanColumns.getOrNull(index - 1)?.status
}

private fun nextStatus(status: TaskStatus): TaskStatus? {
    val index = kanbanColumns.indexOfFirst { it.status == status }
    return kanbanColumns.getOrNull(index + 1)?.status
}
