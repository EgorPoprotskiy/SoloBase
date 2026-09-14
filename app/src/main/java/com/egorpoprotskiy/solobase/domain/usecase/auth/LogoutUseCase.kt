package com.egorpoprotskiy.solobase.domain.usecase.auth

import com.egorpoprotskiy.solobase.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke() {
        authRepository.logout()
    }
}