package com.egorpoprotskiy.solobase.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egorpoprotskiy.solobase.R
import com.egorpoprotskiy.solobase.ui.tasks.components.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    // Получаем вьюмодель через Hilt
    viewModel: TaskViewModel = hiltViewModel()
) {
    // Подписываемся на список задач
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    //переменная для диалогового окна создания новой задачи
    var showDialog by remember() { mutableStateOf(false) }
    var taskText by remember() { mutableStateOf("")}
    // Базовая обертка для экрана
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои задачи") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {showDialog = true},
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_new_task)
                )
            }
        }
    ) { paddingValues ->
        // Проверяем, есть ли задачи
        if (tasks.isEmpty()) {
            EmptyState(modifier = Modifier.padding(paddingValues))
        } else {
            // Список задач
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = tasks,
                    key = { it.id } // Важно для оптимизации анимаций и прокрутки
                ) { task ->
                    TaskItem(
                        task = task,
                        onCheckedChange = { isChecked ->
                            viewModel.onTaskChecked(task, isChecked)
                        },
                        onClick = {
//                        viewModel.onTaskClicked(task)
                        }
                    )
                }
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    taskText = ""},
                title = {
                    Text(
                        stringResource(R.string.add_new_task))

                },
                text = {
                    OutlinedTextField(
                        value = taskText,
                        onValueChange = { taskText = it}, // Обновляем состояние при наборе
                        label = { Text("Что нужно сделать")},
//                        modifier = Modifier.fillMaxSize(),
                        singleLine = false
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (taskText.isNotBlank()) {
                                showDialog = false
                                taskText = ""
                            }
                        }
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
                        }
                    ) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }
    }
}


@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Список пуст",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Добавьте свою первую задачу",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
