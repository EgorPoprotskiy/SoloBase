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
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.egorpoprotskiy.solobase.ui.notes.ProjectNotesViewModel
import com.egorpoprotskiy.solobase.ui.notes.ProjectNotesScreen
import com.egorpoprotskiy.solobase.ui.projects.ProjectsScreen
import com.egorpoprotskiy.solobase.ui.projects.ProjectViewModel
import com.egorpoprotskiy.solobase.ui.tasks.SearchTextField
import com.egorpoprotskiy.solobase.ui.tasks.TaskViewModel
import com.egorpoprotskiy.solobase.ui.tasks.TasksControlsMenu
import com.egorpoprotskiy.solobase.ui.tasks.TasksScreen

@Composable
fun MainScreen(
    taskViewModel: TaskViewModel = hiltViewModel(),
    projectNotesViewModel: ProjectNotesViewModel = hiltViewModel(),
    projectViewModel: ProjectViewModel = hiltViewModel()
) {
    var selectedSection by remember { mutableStateOf(MainSection.TASKS) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var selectedProjectSection by remember { mutableStateOf(ProjectSection.TASKS) }
    var projectControlsMenuExpanded by remember { mutableStateOf(false) }
    var projectSearchMode by remember { mutableStateOf(false) }
    val taskUiState by taskViewModel.uiState.collectAsStateWithLifecycle()
    val notesUiState by projectNotesViewModel.uiState.collectAsStateWithLifecycle()
    val projectsUiState by projectViewModel.uiState.collectAsStateWithLifecycle()

    fun closeSelectedProject() {
        taskViewModel.updateSearchQuery("")
        projectNotesViewModel.updateSearchQuery("")
        projectControlsMenuExpanded = false
        projectSearchMode = false
        selectedProject = null
        selectedProjectSection = ProjectSection.TASKS
        selectedSection = MainSection.PROJECTS
    }

    BackHandler(enabled = selectedProject != null) {
        closeSelectedProject()
    }

    LaunchedEffect(selectedProject?.id, projectsUiState.projects) {
        val currentProject = selectedProject ?: return@LaunchedEffect
        selectedProject = projectsUiState.projects.firstOrNull { it.id == currentProject.id }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                tonalElevation = NavigationBarDefaults.Elevation
            ) {
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
                        label = { Text(section.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                            searchMode = projectSearchMode,
                            searchQuery = if (selectedProjectSection == ProjectSection.TASKS) {
                                taskUiState.searchQuery
                            } else {
                                notesUiState.searchQuery
                            },
                            searchPlaceholder = if (selectedProjectSection == ProjectSection.TASKS) {
                                "Поиск задач"
                            } else {
                                "Поиск заметок"
                            },
                            onBackClick = ::closeSelectedProject,
                            onProjectSectionSelected = {
                                projectSearchMode = false
                                taskViewModel.updateSearchQuery("")
                                projectNotesViewModel.updateSearchQuery("")
                                selectedProjectSection = it
                            },
                            onControlsMenuExpandedChange = { projectControlsMenuExpanded = it },
                            onDisplayModeSelected = taskViewModel::selectDisplayMode,
                            onFilterSelected = taskViewModel::selectFilter,
                            onSearchClick = { projectSearchMode = true },
                            onSearchQueryChange = {
                                if (selectedProjectSection == ProjectSection.TASKS) {
                                    taskViewModel.updateSearchQuery(it)
                                } else {
                                    projectNotesViewModel.updateSearchQuery(it)
                                }
                            },
                            onCloseSearch = {
                                projectSearchMode = false
                                if (selectedProjectSection == ProjectSection.TASKS) {
                                    taskViewModel.updateSearchQuery("")
                                } else {
                                    projectNotesViewModel.updateSearchQuery("")
                                }
                            }
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
                                    onBackToProjects = ::closeSelectedProject,
                                    viewModel = taskViewModel
                                )
                                ProjectSection.NOTES -> ProjectNotesScreen(
                                    project = project,
                                    topAppBarWindowInsets = WindowInsets(0.dp),
                                    showTopAppBar = false,
                                    viewModel = projectNotesViewModel
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
                            },
                            viewModel = projectViewModel
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
    searchMode: Boolean,
    searchQuery: String,
    searchPlaceholder: String,
    onBackClick: () -> Unit,
    onProjectSectionSelected: (ProjectSection) -> Unit,
    onControlsMenuExpandedChange: (Boolean) -> Unit,
    onDisplayModeSelected: (com.egorpoprotskiy.solobase.ui.tasks.TasksDisplayMode) -> Unit,
    onFilterSelected: (com.egorpoprotskiy.solobase.ui.tasks.TaskFilter) -> Unit,
    onSearchClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    TopAppBar(
        windowInsets = WindowInsets.statusBars,
        title = {
            if (searchMode) {
                SearchTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = searchPlaceholder,
                    onClose = onCloseSearch
                )
            } else {
                Text(
                    text = project.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
            if (!searchMode) {
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
                IconButton(onClick = onSearchClick) {
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
                    onExpandedChange = onControlsMenuExpandedChange,
                    onDisplayModeSelected = onDisplayModeSelected,
                    onFilterSelected = onFilterSelected
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
