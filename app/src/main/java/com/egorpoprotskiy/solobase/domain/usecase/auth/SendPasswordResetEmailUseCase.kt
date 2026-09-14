package com.egorpoprotskiy.solobase.domain.usecase.auth

import com.egorpoprotskiy.solobase.domain.repository.AuthRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun  invoke (
        email: String
    ): Result<Unit> {
        return authRepository.sendPasswordResetEmail(
            email = email
        )
    }
}