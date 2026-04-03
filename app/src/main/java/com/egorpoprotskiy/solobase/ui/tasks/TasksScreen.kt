package com.egorpoprotskiy.solobase.ui.tasks

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egorpoprotskiy.solobase.ui.tasks.components.TaskItem

@Composable
fun TasksScreen(
    // Получаем вьюмодель через Hilt
    viewModel: TaskViewModel = hiltViewModel()
) {
    // Подписываемся на список задач
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    // Базовая обертка для экрана
    Scaffold(
        topBar = {
            Text(
                "Мои задачи",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    ) { paddingValues ->
        //Список задач
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp) // Место для будущей кнопки FAB
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
}