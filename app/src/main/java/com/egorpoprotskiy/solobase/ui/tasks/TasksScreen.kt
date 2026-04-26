package com.egorpoprotskiy.solobase.ui.tasks

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egorpoprotskiy.solobase.R
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.ui.tasks.components.TaskItem
import com.egorpoprotskiy.solobase.ui.theme.ImportantGold
import com.egorpoprotskiy.solobase.ui.theme.UrgentRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    // Получаем вьюмодель через Hilt
    viewModel: TaskViewModel = hiltViewModel()
) {
    // Подписываемся на список задач
    val tasks by viewModel.tasks.collectAsStateWithLifecycle(initialValue = emptyList())
    val displayMode by viewModel.displayMode.collectAsState()
    //переменная для диалогового окна создания новой задачи
    var showDialog by remember() { mutableStateOf(false) }
    var taskText by remember() { mutableStateOf("") }
    //Диалоговое окно на удаление задачи
    var showDeleteDialog by remember() { mutableStateOf(false) }
    var taskToDelete by remember() { mutableStateOf<Task?>(null) }
    // Разделение экрана кнопкой на общий список задач и на матрицу
    var isMatrixMode by remember { mutableStateOf(false) } // По умолчанию — обычный список
    // Отслеживание состояний isUrgent и isImportant
    var isUrgent by remember { mutableStateOf(false) }
    var isImportant by remember { mutableStateOf(false) }
    //Переменная для анимации(чтобы иконка плавно вращалась)
    val rotation by animateFloatAsState(
        targetValue = if (displayMode == TasksDisplayMode.MATRIX) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "IconRotation"
    )
    // Базовая обертка для экрана
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tasks_title)) },
                actions = {
                    // Кнопка переключения режимов
                    IconButton(
                        onClick = {
                            viewModel.toggleDisplayMode()
                        }
                    ) {
                        Icon(
                            imageVector = if (displayMode == TasksDisplayMode.LIST)
                                Icons.Default.GridView else Icons.Default.List,
                            contentDescription = stringResource(R.string.change_mode),
                            modifier = Modifier.rotate(rotation) // Добавляем вращение
                            // Явно задаем размер, чтобы иконка не казалась мелкой
//                            modifier = Modifier.size(24.dp),
                            // Используем onPrimary, так как фон шапки у нас SoloGreen (Primary)
//                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Наш SoloGreen
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary // Контрастный белый/светлый
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)),
                shape = MaterialTheme.shapes.large,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_new_task)
                )
            }
        }
    ) { paddingValues ->
        //Анимация переключения между списком задач и матрицей.
        AnimatedContent(
            targetState = displayMode,
            label = "ModeAnimation",
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.92f) togetherWith
                        fadeOut(animationSpec = tween(300))
            }
        ) { targetMode ->
            if (targetMode == TasksDisplayMode.MATRIX) {
                EisenhowerMatrix(
                    tasks = tasks,
                    // Вот решение твоей "красной" ошибки:
                    onTaskChecked = { task, isCompleted ->
                        viewModel.onTaskChecked(
                            task,
                            isCompleted
                        ) // Проверь название метода во ViewModel!
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                // Список задач
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    taskText = ""
                    isUrgent = false // Сбрасываем при закрытии
                    isImportant = false
                },
                title = { Text(stringResource(R.string.add_new_task)) },
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
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (taskText.isNotBlank()) {
                                viewModel.addTask(taskText, isUrgent, isImportant)
                                // Тут позже вызовем метод ViewModel
                                showDialog = false
                                taskText = ""
                                isUrgent = false
                                isImportant = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            stringResource(R.string.add_new_task)
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