package com.egorpoprotskiy.solobase.di

import android.content.Context
import com.egorpoprotskiy.solobase.data.auth.FirebaseAuthRepositoryImpl
import com.egorpoprotskiy.solobase.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Hilt-модуль для зависимостей Firebase Authentication.
/* Здесь создаются FirebaseAuth и реализация AuthRepository.
* Благодаря этому остальные слои приложения не должны самостоятельно
* создавать FirebaseAuth или FirebaseAuthRepositoryImpl.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    //Предоставляет единственный экземпляр FirebaseAuth на протяжении жизни приложения.
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    //Связывает domain-интерфейс AuthRepository с Firebase-реализацией из data-слоя.
    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository{
        return FirebaseAuthRepositoryImpl(auth)
    }
}