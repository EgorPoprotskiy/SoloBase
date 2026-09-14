package com.egorpoprotskiy.solobase.ui.auth

import androidx.compose.runtime.currentComposer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.solobase.domain.usecase.auth.GetCurrentUserUseCase
import com.egorpoprotskiy.solobase.domain.usecase.auth.LoginUseCase
import com.egorpoprotskiy.solobase.domain.usecase.auth.LogoutUseCase
import com.egorpoprotskiy.solobase.domain.usecase.auth.RegisterUseCase
import com.egorpoprotskiy.solobase.domain.usecase.auth.SendPasswordResetEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.egorpoprotskiy.solobase.domain.models.AuthError
import com.egorpoprotskiy.solobase.domain.models.AuthException

/**
 * ViewModel экрана авторизации.
 *
 * Получает действия от UI через AuthUiEvent,
 * вызывает соответствующий UseCase и обновляет AuthUiState.
 */

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(currentUser = getCurrentUserUseCase()))
    //Публичное состояние, доступное Compose UI только для чтения.
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    //Обрабатывает события от AuthScreen.
    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.Login -> login(
                email = event.email,
                password = event.password
            )
            is AuthUiEvent.Register -> register (
                email = event.email,
                password = event.password
            )
            AuthUiEvent.Logout -> logout()
            is AuthUiEvent.SendPasswordResetEmail -> sendPasswordResetEmail(
                email = event.email
            )
            AuthUiEvent.ClearError -> clearError()
            AuthUiEvent.ClearSuccessMessage -> clearSuccessMessage()
        }
    }
    //Выполняет вход пользователя.
    private fun login(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            setLoading(true)
            loginUseCase(
                email = email,
                password = password
            ).onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentUser = user,
                    errorMessage = null,
                    successMessage = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = getErrorMessage(error)
                )
            }
        }
    }
    //Выполняет регистрацию пользователя.
    private fun register(
        email: String,
        password: String
    ){
        viewModelScope.launch {
            setLoading(true)
            registerUseCase(
                email = email,
                password = password
            ).onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentUser = user,
                    errorMessage = null,
                    successMessage = null

                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = getErrorMessage(error)
                )
            }
        }
    }
    // Выполняет выход из текущего аккаунта.
    private fun logout() {
        logoutUseCase()
        _uiState.value = _uiState.value.copy(
            currentUser = null,
            errorMessage = null
        )
    }
    // Отправляет email для сброса пароля.
    private fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            setLoading(true)
            sendPasswordResetEmailUseCase(email).onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null,
                    successMessage = "Password reset email sent"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = getErrorMessage(error),
                    successMessage = null
                )
            }
        }
    }
    private fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(
            successMessage = null
        )
    }
    //Очищает сообщение об ошибке.
    private fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
    //Устанавливает состояние загрузки.
    private fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(
            isLoading = isLoading
        )
    }
}
/**
 * Преобразует ошибку domain-слоя в понятное сообщение для UI.
 *
 * ViewModel не знает ничего о Firebase.
 */
private fun getErrorMessage(exception: Throwable): String {
    val authError = (exception as? AuthException)?.error

    return when (authError) {
        AuthError.InvalidEmail ->
            "Некорректный email"

        AuthError.WeakPassword ->
            "Слишком слабый пароль"

        AuthError.WrongCredentials ->
            "Неверный email или пароль"

        AuthError.EmailAlreadyInUse ->
            "Этот email уже зарегистрирован"

        AuthError.TooManyRequests ->
            "Слишком много попыток. Попробуйте позже"

        AuthError.NetworkError ->
            "Ошибка сети. Проверьте подключение к интернету"

        AuthError.Unknown ->
            "Не удалось выполнить операцию"

        null ->
            "Не удалось выполнить операцию"
    }
}