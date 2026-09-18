package com.egorpoprotskiy.solobase.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egorpoprotskiy.solobase.ui.MainScreen

/**
 * Корневой экран приложения.
 *
 * Определяет, какой экран должен отображаться:
 * - AuthScreen, если пользователь не авторизован;
 * - MainScreen, если пользователь авторизован.
 *
 * AuthViewModel хранит состояние текущего пользователя.
 */
@Composable
fun RootScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.currentUser == null) {
        AuthScreen(
            viewModel = viewModel
        )
    } else {
        MainScreen(
            onLogout = {
                viewModel.onEvent(AuthUiEvent.Logout)
            }
        )
    }
}