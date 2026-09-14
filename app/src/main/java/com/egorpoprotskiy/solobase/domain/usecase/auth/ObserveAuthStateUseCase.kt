package com.egorpoprotskiy.solobase.domain.usecase.auth

import com.egorpoprotskiy.solobase.domain.models.User
import com.egorpoprotskiy.solobase.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Наблюдает за текущим состоянием авторизации пользователя.
 */
class ObserveAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.observeAuthState()
    }
}