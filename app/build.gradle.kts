plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // ✅ KSP
    alias(libs.plugins.ksp)
    // ✅ Hilt
    alias(libs.plugins.hilt.android)
    //✅Serialization
    alias(libs.plugins.kotlin.serialization)

}

android {
    namespace = "com.egorpoprotskiy.solobase"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.egorpoprotskiy.solobase"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}
kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    //✅Navigation
    implementation(libs.androidx.navigation.compose)
    //✅Hilt - основной(retrofit, coil и т.д.)
    implementation(libs.hilt.android) // Основная библиотека
    ksp(libs.hilt.compiler) //ГЕНЕРАТОР КОДА ЧЕРЕЗ KSP (то, ради чего всё настраивается), делает магию для @HiltViewModel и @Inject.
    //✅Hilt - компилятор для AndroidX(Compose Navigation/ViewModel и WorkManager)
    ksp(libs.hilt.androidx.compiler) // Дополнительный от AndroidX, делает магию для @HiltWorker.
    implementation(libs.androidx.hilt.navigation.compose) // Интеграция с Compose Navigation/ViewModel
    implementation(libs.androidx.hilt.work) //Hilt для WorkManager
    //✅WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    //✅Retrofit - для работы с сетью
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    //✅Coil - для работы с изображениями. Coil 3 (Новое поколение)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.coil.video)
    //✅Serialization - механизм от JetBrains, который работает быстрее и чище, чем старый GSON
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    //✅Lottie - для утонченной анимации.
    implementation(libs.lottie.compose)
    //✅LifeCycle(Для ViewModel) - жизненный цикл для работы с ViewModel и Compose.
    implementation(libs.androidx.lifecycle.viewmodel.compose) // Для hiltViewModel() и работы во вьюхах
    implementation(libs.androidx.lifecycle.runtime.compose)   // Для collectAsStateWithLifecycle() - это безопаснее для памяти
    //✅Icon - набор иконок от Google - много весит(не всегда нужен)
    implementation(libs.androidx.material.icons.extended)
    //✅ Библиотека для работы с шрифтами от Google
    implementation(libs.androidx.compose.ui.text.google.fonts)
}