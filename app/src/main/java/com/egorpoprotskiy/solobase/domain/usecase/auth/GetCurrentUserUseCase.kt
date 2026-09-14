package com.egorpoprotskiy.solobase.domain.usecase.auth

import com.egorpoprotskiy.solobase.domain.models.User
import com.egorpoprotskiy.solobase.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}