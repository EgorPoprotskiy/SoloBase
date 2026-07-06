package com.egorpoprotskiy.solobase.ui.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egorpoprotskiy.solobase.R
import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.ui.tasks.components.TaskItem
import com.egorpoprotskiy.solobase.ui.tasks.components.formatReminderAt
import com.egorpoprotskiy.solobase.ui.theme.ImportantGold
import com.egorpoprotskiy.solobase.ui.theme.UrgentRed
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    selectedProject: Project? = null,
    onBackToProjects: () -> Unit = {},
    topAppBarWindowInsets: WindowInsets = WindowInsets.statusBars,
    showTopAppBar: Boolean = true,
    // Получаем вьюмодель через Hilt
    viewModel: TaskViewModel = hiltViewModel()
) {
    // Подписываемся на список задач
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tasks = uiState.tasks
    val displayMode = uiState.displayMode
    val selectedFilter = uiState.selectedFilter
    val searchQuery = uiState.searchQuery
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(selectedProject?.id) {
        viewModel.selectProject(selectedProject?.id)
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is TasksUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }
    //переменная для диалогового окна создания новой задачи
    var showDialog by remember() { mutableStateOf(false) }
    var taskText by remember() { mutableStateOf("") }
    //Диалоговое окно на удаление задачи
    var showDeleteDialog by remember() { mutableStateOf(false) }
    var taskToDelete by remember() { mutableStateOf<Task?>(null) }
    // Отслеживание состояний isUrgent и isImportant
    var isUrgent by remember { mutableStateOf(false) }
    var isImportant by remember { mutableStateOf(false) }
    var reminderAt by remember { mutableStateOf<Long?>(null) }
    var controlsMenuExpanded by remember { mutableStateOf(false) }
    var searchMode by remember { mutableStateOf(false) }
    //Переменная для редактирования заметки(повторное открытие dialogAlert)
    var editingTask by remember { mutableStateOf<Task?>(null) }
    // Базовая обертка для экрана
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = if (showTopAppBar) {
            {
                TopAppBar(
                    windowInsets = topAppBarWindowInsets,
                    title = {
                        if (searchMode) {
                            SearchTextField(
                                value = searchQuery,
                                onValueChange = viewModel::updateSearchQuery,
                                placeholder = "Поиск задач",
                                onClose = {
                                    searchMode = false
                                    viewModel.updateSearchQuery("")
                                }
                            )
                        } else {
                            Text(
                                text = selectedProject?.name ?: stringResource(R.string.tasks_title),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    navigationIcon = {
                        if (selectedProject != null) {
                            IconButton(onClick = onBackToProjects) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    actions = {
                        if (!searchMode) {
                            IconButton(onClick = { searchMode = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Поиск",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            TasksControlsMenu(
                                displayMode = displayMode,
                                selectedFilter = selectedFilter,
                                expanded = controlsMenuExpanded,
                                onExpandedChange = { controlsMenuExpanded = it },
                                onDisplayModeSelected = viewModel::selectDisplayMode,
                                onFilterSelected = viewModel::selectFilter
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Наш SoloGreen
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary // Контрастный белый/светлый
                    )
                )
            }
        } else {
            {}
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                shape = MaterialTheme.shapes.large,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_new_task)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (selectedFilter != TaskFilter.ALL) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilterChip(
                        selected = true,
                        onClick = { viewModel.selectFilter(TaskFilter.ALL) },
                        label = { Text("${selectedFilter.label} ×") }
                    )
                }
            }
            //Анимация переключения между списком задач и матрицей.
            AnimatedContent(
                targetState = displayMode,
                label = "ModeAnimation",
                modifier = Modifier.weight(1f),
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.92f) togetherWith
                            fadeOut(animationSpec = tween(300))
                }
            ) { targetMode ->
                if (tasks.isEmpty()) {
                    TasksEmptyState(
                        isFiltered = selectedFilter != TaskFilter.ALL,
                        isSearching = searchQuery.isNotBlank(),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    when (targetMode) {
                        TasksDisplayMode.LIST -> {
                            // Список задач
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = tasks,
                                    key = { it.id } // Важно для оптимизации анимаций и прокрутки
                                ) { task ->
                                    TaskItem(
                                        task = task,
                                        //анимация в списке(плавное добавление и удаление задачи)
                                        modifier = Modifier.animateItem(
                                            fadeInSpec = tween(300),
                                            fadeOutSpec = tween(300),
                                            placementSpec = spring(stiffness = Spring.StiffnessLow) // Плавное перемещение
                                        ),
                                        onCheckedChange = { isChecked ->
                                            viewModel.onTaskChecked(task, isChecked)
                                        },
                                        onClick = {
                                            editingTask = task //Запоминаем задачу
                                            taskText = task.content // Предзапоняем текст
                                            isUrgent = task.isUrgent // предзапоняем срочность
                                            isImportant = task.isImportant //Предзаполняем важность
                                            reminderAt = task.reminderAt
                                            showDialog = true // Отерываем тот же самый диалог
//                        viewModel.onTaskClicked(task)
                                        },
                                        onDeleteClick = {
                                            showDeleteDialog = true
                                            taskToDelete = task
//                            viewModel.deleteTask(task.id)
                                        }
                                    )
                                }
                            }
                        }
                        TasksDisplayMode.MATRIX -> {
                            EisenhowerMatrix(
                                tasks = tasks,
                                // Вот решение твоей "красной" ошибки:
                                onTaskChecked = { task, isCompleted ->
                                    viewModel.onTaskChecked(
                                        task,
                                        isCompleted
                                    ) // Проверь название метода во ViewModel!
                                },
                                onTaskLongClick = { task ->
                                    editingTask = task
                                    taskText = task.content
                                    isUrgent = task.isUrgent
                                    isImportant = task.isImportant
                                    reminderAt = task.reminderAt
                                    showDialog = true
                                }
                            )
                        }
                        TasksDisplayMode.KANBAN -> {
                            KanbanBoard(
                                tasks = tasks,
                                onMoveTask = { task, status ->
                                    viewModel.moveTaskToStatus(task, status)
                                },
                                onTaskClick = { task ->
                                    editingTask = task
                                    taskText = task.content
                                    isUrgent = task.isUrgent
                                    isImportant = task.isImportant
                                    reminderAt = task.reminderAt
                                    showDialog = true
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    editingTask = null // очищаем после закрытия
                    taskText = ""
                    isUrgent = false // Сбрасываем при закрытии
                    isImportant = false
                    reminderAt = null
                },
                title = {
                    Text(stringResource(R.string.save_task)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = taskText,
                            onValueChange = { taskText = it }, // Обновляем состояние при наборе
                            label = { Text(stringResource(R.string.task_input_label)) },
                            singleLine = false
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            maxItemsInEachRow = 2 // Ограничит по 2 кнопки в ряд, растягивая их
                        ) {
                            // Кнопка "Срочно"
                            FilterChip(
                                selected = isUrgent,
                                onClick = { isUrgent = !isUrgent },
                                label = {
                                    Text(
                                        "Срочно",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                leadingIcon = { Icon(Icons.Default.Bolt, null) },
                                modifier = Modifier.weight(1f), // Занимает 50% ширины
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = UrgentRed.copy(alpha = 0.2f),
                                    selectedLabelColor = UrgentRed,
                                    selectedLeadingIconColor = UrgentRed
                                )
                            )
                            // Кнопка "Важно"
                            FilterChip(
                                selected = isImportant,
                                onClick = { isImportant = !isImportant },
                                label = {
                                    Text(
                                        "Важно",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                leadingIcon = { Icon(Icons.Default.Star, null) },
                                modifier = Modifier.weight(1f), // Занимает остальные 50%
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ImportantGold.copy(alpha = 0.2f),
                                    selectedLabelColor = ImportantGold,
                                    selectedLeadingIconColor = ImportantGold
                                )
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Напоминание",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = reminderAt?.let { "Напомнить: ${formatReminderAt(it)}" }
                                    ?: "Напоминание не задано",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(
                                    onClick = {
                                        showReminderDateTimePicker(
                                            context = context,
                                            initialReminderAt = reminderAt,
                                            onReminderSelected = { selectedReminderAt ->
                                                reminderAt = selectedReminderAt
                                            }
                                        )
                                    }
                                ) {
                                    Text(if (reminderAt == null) "Выбрать" else "Изменить")
                                }
                                if (reminderAt != null) {
                                    TextButton(onClick = { reminderAt = null }) {
                                        Text("Очистить")
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (taskText.isNotBlank()) {
                                if (editingTask == null) {
                                    //Создать новую
                                    viewModel.addTask(taskText, isUrgent, isImportant, reminderAt)
                                } else {
                                    //Редактировать существующую
                                    viewModel.updateTaskDetails(
                                        task = editingTask!!,
                                        content = taskText,
                                        isUrgent = isUrgent,
                                        isImportant = isImportant,
                                        reminderAt = reminderAt
                                    )
                                }
                                // Тут позже вызовем метод ViewModel
                                showDialog = false
                                editingTask = null
                                taskText = ""
                                isUrgent = false
                                isImportant = false
                                reminderAt = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            stringResource(R.string.save_task)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            taskText = ""
                            isUrgent = false
                            isImportant = false
                            reminderAt = null
                        }
                    ) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }
        //Диалоговое окно на удаление задачи
        if (showDeleteDialog && taskToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    // Закрываем диалог при нажатии вне его или кнопки "Назад"
                    showDeleteDialog = false
                    taskToDelete = null
                },
                title = {
                    Text(stringResource(R.string.delete_confirmation_title))
                },
                text = {
                    Text(
                        stringResource(
                            R.string.delete_confirmation_message,
                            taskToDelete!!.content
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Тут вызовем метод ViewModel для удаления задачи
                            viewModel.deleteTask(taskToDelete!!.id)
                            showDeleteDialog = false
                            taskToDelete = null
                        }
                    ) {
                        Text(
                            stringResource(R.string.delete_button),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            taskToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }
    }
}

private fun showReminderDateTimePicker(
    context: Context,
    initialReminderAt: Long?,
    onReminderSelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = initialReminderAt ?: System.currentTimeMillis()
    }

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    onReminderSelected(calendar.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
private fun TasksEmptyState(
    isFiltered: Boolean,
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = when {
                    isSearching -> "Ничего не найдено"
                    isFiltered -> "Нет задач для выбранного фильтра"
                    else -> stringResource(R.string.tasks_empty_title)
                },
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            if (!isSearching) {
                Text(
                    text = if (isFiltered) "Выберите другой фильтр или добавьте новую задачу" else stringResource(R.string.tasks_empty_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TasksControlsMenu(
    displayMode: TasksDisplayMode,
    selectedFilter: TaskFilter,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDisplayModeSelected: (TasksDisplayMode) -> Unit,
    onFilterSelected: (TaskFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        IconButton(onClick = { onExpandedChange(true) }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Настройки отображения",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            Text(
                text = "Вид",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            TasksDisplayMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.label) },
                    leadingIcon = {
                        Icon(
                            imageVector = mode.icon(),
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onDisplayModeSelected(mode)
                        onExpandedChange(false)
                    }
                )
            }
            Text(
                text = "Фильтр",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            TaskFilter.entries.forEach { filter ->
                DropdownMenuItem(
                    text = { Text(filter.label) },
                    onClick = {
                        onFilterSelected(filter)
                        onExpandedChange(false)
                    },
                    trailingIcon = {
                        if (filter == selectedFilter) {
                            Text("✓")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val headerContentColor = MaterialTheme.colorScheme.onPrimary
    val placeholderColor = headerContentColor.copy(alpha = 0.7f)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.titleMedium.copy(
            color = headerContentColor,
            fontWeight = FontWeight.Normal
        ),
        placeholder = {
            Text(
                text = placeholder,
                color = placeholderColor,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Закрыть поиск",
                    tint = headerContentColor
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = headerContentColor,
            unfocusedTextColor = headerContentColor,
            cursorColor = headerContentColor,
            focusedPlaceholderColor = placeholderColor,
            unfocusedPlaceholderColor = placeholderColor,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

private fun TasksDisplayMode.icon(): ImageVector {
    return when (this) {
        TasksDisplayMode.LIST -> Icons.Filled.List
        TasksDisplayMode.MATRIX -> Icons.Filled.GridView
        TasksDisplayMode.KANBAN -> Icons.Filled.Dashboard
    }
}
