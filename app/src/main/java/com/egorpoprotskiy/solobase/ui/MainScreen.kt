package com.egorpoprotskiy.solobase.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.ui.notes.ProjectNotesScreen
import com.egorpoprotskiy.solobase.ui.projects.ProjectsScreen
import com.egorpoprotskiy.solobase.ui.tasks.TaskViewModel
import com.egorpoprotskiy.solobase.ui.tasks.TasksControlsMenu
import com.egorpoprotskiy.solobase.ui.tasks.TasksScreen

@Composable
fun MainScreen(
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    var selectedSection by remember { mutableStateOf(MainSection.TASKS) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var selectedProjectSection by remember { mutableStateOf(ProjectSection.TASKS) }
    var projectControlsMenuExpanded by remember { mutableStateOf(false) }
    val taskUiState by taskViewModel.uiState.collectAsStateWithLifecycle()

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
                            TasksScreen(viewModel = taskViewModel)
                        }
                    } else {
                        ProjectHeader(
                            project = project,
                            selectedProjectSection = selectedProjectSection,
                            controlsMenuExpanded = projectControlsMenuExpanded,
                            displayMode = taskUiState.displayMode,
                            selectedFilter = taskUiState.selectedFilter,
                            onBackClick = {
                                selectedProject = null
                                selectedProjectSection = ProjectSection.TASKS
                                selectedSection = MainSection.PROJECTS
                            },
                            onProjectSectionSelected = { selectedProjectSection = it },
                            onControlsMenuExpandedChange = { projectControlsMenuExpanded = it },
                            onDisplayModeSelected = taskViewModel::selectDisplayMode,
                            onFilterSelected = taskViewModel::selectFilter
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            when (selectedProjectSection) {
                                ProjectSection.TASKS -> TasksScreen(
                                    selectedProject = project,
                                    topAppBarWindowInsets = WindowInsets(0.dp),
                                    showTopAppBar = false,
                                    onBackToProjects = {
                                        selectedProject = null
                                        selectedProjectSection = ProjectSection.TASKS
                                        selectedSection = MainSection.PROJECTS
                                    },
                                    viewModel = taskViewModel
                                )
                                ProjectSection.NOTES -> ProjectNotesScreen(
                                    project = project,
                                    topAppBarWindowInsets = WindowInsets(0.dp),
                                    showTopAppBar = false
                                )
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ProjectHeader(
    project: Project,
    selectedProjectSection: ProjectSection,
    controlsMenuExpanded: Boolean,
    displayMode: com.egorpoprotskiy.solobase.ui.tasks.TasksDisplayMode,
    selectedFilter: com.egorpoprotskiy.solobase.ui.tasks.TaskFilter,
    onBackClick: () -> Unit,
    onProjectSectionSelected: (ProjectSection) -> Unit,
    onControlsMenuExpandedChange: (Boolean) -> Unit,
    onDisplayModeSelected: (com.egorpoprotskiy.solobase.ui.tasks.TasksDisplayMode) -> Unit,
    onFilterSelected: (com.egorpoprotskiy.solobase.ui.tasks.TaskFilter) -> Unit
) {
    TopAppBar(
        windowInsets = WindowInsets.statusBars,
        title = {
            Text(
                text = project.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = { onProjectSectionSelected(ProjectSection.TASKS) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "Задачи",
                    tint = if (selectedProjectSection == ProjectSection.TASKS) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.55f)
                    }
                )
            }
            IconButton(onClick = { onProjectSectionSelected(ProjectSection.NOTES) }) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Заметки",
                    tint = if (selectedProjectSection == ProjectSection.NOTES) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.55f)
                    }
                )
            }
            TasksControlsMenu(
                displayMode = displayMode,
                selectedFilter = selectedFilter,
                expanded = controlsMenuExpanded,
                onExpandedChange = onControlsMenuExpandedChange,
                onDisplayModeSelected = onDisplayModeSelected,
                onFilterSelected = onFilterSelected
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
