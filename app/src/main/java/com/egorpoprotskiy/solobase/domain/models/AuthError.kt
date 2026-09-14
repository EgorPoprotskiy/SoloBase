package com.egorpoprotskiy.solobase.domain.models
/**
 * Возможные ошибки авторизации.
 *
 * Domain-слой не знает ничего о Firebase.
 * Каждый вариант описывает только смысл ошибки.
 */
sealed interface AuthError{
    data object InvalidEmail: AuthError
    data object WeakPassword: AuthError
    data object WrongCredentials: AuthError
    data object EmailAlreadyInUse: AuthError
    data object TooManyRequests: AuthError
    data object NetworkError: AuthError
    data object Unknown: AuthError
}
/**
 * Исключение приложения для передачи ошибки авторизации
 * через Result.failure().
 */
class AuthException(
    val error: AuthError
) : Exception()

