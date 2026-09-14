package com.egorpoprotskiy.solobase.ui.auth

import com.egorpoprotskiy.solobase.domain.models.User
/**
 * Состояние экрана авторизации.
 *
 * ViewModel изменяет это состояние,
 * а Compose UI наблюдает за ним и отображает соответствующий экран.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)