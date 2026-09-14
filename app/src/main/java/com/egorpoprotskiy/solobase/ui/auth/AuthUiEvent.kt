package com.egorpoprotskiy.solobase.ui.auth
/**
 * Одноразовые действия пользователя на экране авторизации.
 *
 * Каждое событие представляет конкретное действие.
 * Это не состояние UI, а команда для ViewModel.
 */
sealed interface AuthUiEvent {
    //Пользователь пытается войти в аккаунт.
    data class Login(
        val email: String,
        val password: String
    ): AuthUiEvent
    //Пользователь создаёт новый аккаунт.
    data class Register(
        val email: String,
        val password: String
    ): AuthUiEvent
    //Пользователь выходит из аккаунта.
    data object Logout: AuthUiEvent
    //Пользователь запрашивает восстановление пароля.
    data class SendPasswordResetEmail(
        val email: String
    ): AuthUiEvent
    //Пользователь закрывает отображённую ошибку.
    data object ClearError: AuthUiEvent
    data object ClearSuccessMessage: AuthUiEvent
}