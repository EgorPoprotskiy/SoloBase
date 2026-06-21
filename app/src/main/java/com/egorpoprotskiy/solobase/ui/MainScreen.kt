package com.egorpoprotskiy.solobase.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
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

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MainSection.entries.forEach { section ->
                FilterChip(
                    selected = selectedSection == section,
                    onClick = {
                        selectedSection = section
                        if (section == MainSection.TASKS) {
                            selectedProject = null
                            selectedProjectSection = ProjectSection.TASKS
                        }
                    },
                    label = { Text(section.label) }
                )
            }
        }
        when (selectedSection) {
            MainSection.TASKS -> {
                val project = selectedProject
                if (project == null) {
                    TasksScreen()
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
            MainSection.PROJECTS -> ProjectsScreen(
                onProjectClick = { project ->
                    selectedProject = project
                    selectedProjectSection = ProjectSection.TASKS
                    selectedSection = MainSection.TASKS
                }
            )
        }
    }
}

private enum class MainSection(val label: String) {
    TASKS("Задачи"),
    PROJECTS("Проекты")
}

private enum class ProjectSection(val label: String) {
    TASKS("Задачи"),
    NOTES("Заметки")
}
