package com.egorpoprotskiy.solobase.data.auth

import com.egorpoprotskiy.solobase.domain.models.AuthError
import com.google.firebase.auth.FirebaseAuthException

/**
 * Преобразует ошибки Firebase Authentication
 * в ошибки, понятные domain-слою приложения.
 *
 * Благодаря этому FirebaseAuthException не выходит
 * за пределы data-слоя.
 */
object FirebaseAuthErrorMapper {
    /**
     * Определяет тип ошибки по коду Firebase Authentication.
     */
    fun map(exception: FirebaseAuthException): AuthError {
        return when(exception.errorCode) {
            // Email имеет неправильный формат.
            "ERROR_INVALID_EMAIL" ->
                AuthError.InvalidEmail

            // Пароль не соответствует требованиям Firebase.
            "ERROR_WEAK_PASSWORD" ->
                AuthError.WeakPassword

            // Неверные учётные данные при входе.
            "ERROR_WRONG_PASSWORD",
            "ERROR_USER_NOT_FOUND",
            "ERROR_INVALID_CREDENTIAL" ->
                AuthError.WrongCredentials

            // Такой email уже зарегистрирован.
            "ERROR_EMAIL_ALREADY_IN_USE" ->
                AuthError.EmailAlreadyInUse

            // Слишком много запросов за короткий промежуток времени.
            "ERROR_TOO_MANY_REQUESTS" ->
                AuthError.TooManyRequests

            // Ошибки сети.
            "ERROR_NETWORK_REQUEST_FAILED" ->
                AuthError.NetworkError

            // Всё остальное.
            else ->
                AuthError.Unknown
        }
    }
}