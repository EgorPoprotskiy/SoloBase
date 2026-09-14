package com.egorpoprotskiy.solobase.data.auth

import com.egorpoprotskiy.solobase.domain.models.AuthError
import com.egorpoprotskiy.solobase.domain.models.AuthException
import com.egorpoprotskiy.solobase.domain.models.User
import com.egorpoprotskiy.solobase.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Firebase-реализация AuthRepository.
 *
 * Этот класс связывает domain-слой приложения с Firebase Authentication.
 * Firebase SDK используется только в data-слое.
 *
 * UI → ViewModel → UseCase → AuthRepository → FirebaseAuth
 */
class FirebaseAuthRepositoryImpl (
    private val auth: FirebaseAuth
) : AuthRepository {
    //Регистрирует нового пользователя через Firebase Authentication. После успешной регистрации Firebase автоматически авторизует созданного пользователя.
    override suspend fun register(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()
            val firebaseUser = result.user
                ?: return Result.failure(
                    IllegalStateException("Firebase user is null after registration")
                )
            Result.success(firebaseUser.toDomainUser())
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }
    //Выполняет вход существующего пользователя через Firebase.
    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val result = auth
                .signInWithEmailAndPassword(email, password)
                .await()
            val firebaseUser = result.user
                ?: return Result.failure(
                    IllegalStateException("Firebase user is null after login")
                )
            Result.success(firebaseUser.toDomainUser())
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }
    //Завершает текущую Firebase-сессию.
    override fun logout() {
        auth.signOut()
    }
    //Возвращает текущего авторизованного пользователя. Firebase возвращает null, если пользователь не авторизован.
    override fun getCurrentUser(): User? {
        return auth.currentUser?.toDomainUser()
    }
    //Отправляет пользователю письмо для восстановления пароля.
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }
    //Преобразует FirebaseUser в domain-модель User. Благодаря этому FirebaseUser не выходит за пределы data-слоя.
    private fun FirebaseUser.toDomainUser(): User {
        return User(
            id = uid,
            email = email.orEmpty()
        )
    }
    /**
     * Наблюдает за изменением состояния Firebase Authentication.
     *
     * Flow получает нового пользователя при входе, выходе
     * или другом изменении состояния Firebase-сессии.
     */
    override fun observeAuthState(): Flow<User?> = callbackFlow {
        val listener = AuthStateListener { firebaseAuth ->
            trySend(
                firebaseAuth.currentUser?.toDomainUser()
            )
        }

        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }
}

/**
 * Преобразует исключение Firebase в AuthException.
 */
private fun mapAuthException(exception: Exception): AuthException {
    return if (exception is FirebaseAuthException) {
        AuthException(
            FirebaseAuthErrorMapper.map(exception)
        )
    } else {
        AuthException(AuthError.Unknown)
    }
}

