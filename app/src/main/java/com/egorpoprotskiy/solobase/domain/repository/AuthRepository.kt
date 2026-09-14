package com.egorpoprotskiy.solobase.domain.repository

import com.egorpoprotskiy.solobase.domain.models.User
/**
 * Абстракция авторизации для domain-слоя.
 * Реализация будет находиться в data/auth и использовать Firebase Authentication.
 */
interface AuthRepository {
    //Регистрирует нового пользователя по email и паролю.
    suspend fun register(
        email: String,
        password: String
    ): Result<User>

    //Выполняет вход существующего пользователя.
    suspend fun login(
        email: String,
        password: String
    ): Result<User>

    //Выходит из текущего аккаунта.
    fun logout()

    //Возвращает текущего авторизованного пользователя, либо null, если пользователь не авторизован.
    fun getCurrentUser(): User?

    //Отправляет письмо для восстановления пароля.
    suspend fun sendPasswordResetEmail(
        email: String
    ): Result<Unit>
}