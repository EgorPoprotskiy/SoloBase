package com.egorpoprotskiy.solobase.domain.usecase.auth

import com.egorpoprotskiy.solobase.domain.models.User
import com.egorpoprotskiy.solobase.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Выполняет вход пользователя в SoloBase.
 *
 * ViewModel вызывает UseCase, а UseCase передаёт запрос
 * в AuthRepository. Конкретная реализация Repository
 * (Firebase или другая) domain-слою не важна.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> {
        return authRepository.login(
            email = email,
            password = password
        )
    }
}