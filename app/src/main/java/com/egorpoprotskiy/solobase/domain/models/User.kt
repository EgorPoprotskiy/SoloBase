package com.egorpoprotskiy.solobase.domain.models

data class User (
    /**
     * Domain-модель авторизованного пользователя.
     *
     * Firebase UID хранится в id и в дальнейшем будет использоваться
     * для привязки облачных данных SoloBase к конкретному пользователю.
     *
     * Firebase SDK здесь не используется: domain-слой не должен зависеть
     * от конкретной технологии авторизации.
     */
    val id: String,
    val email: String
)