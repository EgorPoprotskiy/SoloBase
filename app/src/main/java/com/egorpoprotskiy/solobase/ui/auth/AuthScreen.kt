package com.egorpoprotskiy.solobase.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egorpoprotskiy.solobase.ui.auth.components.AuthForm

/**
 * Главный экран авторизации.
 *
 * AuthViewModel предоставляет состояние и обрабатывает действия пользователя.
 * AuthScreen отвечает только за отображение состояния.
 */

@Composable
fun AuthScreen(
//    uiState: AuthUiState,
//    onEvent: (AuthUiEvent) -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
    modifier = modifier
        .fillMaxSize()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SoloBase"
        )
        AuthForm(
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onEvent = viewModel::onEvent
        )
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}