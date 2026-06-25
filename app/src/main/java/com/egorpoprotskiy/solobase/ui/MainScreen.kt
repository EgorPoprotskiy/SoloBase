package com.egorpoprotskiy.solobase.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.ui.notes.ProjectNotesScreen
import com.egorpoprotskiy.solobase.ui.projects.ProjectsScreen
import com.egorpoprotskiy.solobase.ui.tasks.TasksScreen

@Composable
fun MainScreen() {
    var selectedSection by remember { mutableStateOf(MainSection.TASKS) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var selectedProjectSection by remember { mutableStateOf(ProjectSection.TASKS) }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            NavigationBar {
                MainSection.entries.forEach { section ->
                    NavigationBarItem(
                        selected = selectedSection == section,
                        onClick = {
                            selectedSection = section
                            if (section == MainSection.TASKS) {
                                selectedProject = null
                                selectedProjectSection = ProjectSection.TASKS
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = section.icon,
                                contentDescription = null
                            )
                        },
                        label = { Text(section.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedSection) {
                MainSection.TASKS -> {
                    val project = selectedProject
                    if (project == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            TasksScreen()
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ProjectSection.entries.forEach { section ->
                                FilterChip(
                                    selected = selectedProjectSection == section,
                                    onClick = { selectedProjectSection = section },
                                    label = { Text(section.label) }
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            when (selectedProjectSection) {
                                ProjectSection.TASKS -> TasksScreen(
                                    selectedProject = project,
                                    onBackToProjects = {
                                        selectedProject = null
                                        selectedProjectSection = ProjectSection.TASKS
                                        selectedSection = MainSection.PROJECTS
                                    }
                                )
                                ProjectSection.NOTES -> ProjectNotesScreen(project = project)
                            }
                        }
                    }
                }
                MainSection.PROJECTS -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        ProjectsScreen(
                            onProjectClick = { project ->
                                selectedProject = project
                                selectedProjectSection = ProjectSection.TASKS
                                selectedSection = MainSection.TASKS
                            }
                        )
                    }
                }
            }
        }
    }
}

private enum class MainSection(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    TASKS("Задачи", Icons.AutoMirrored.Filled.List),
    PROJECTS("Проекты", Icons.Default.Folder)
}

private enum class ProjectSection(val label: String) {
    TASKS("Задачи"),
    NOTES("Заметки")
}
