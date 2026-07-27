import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.cielo.cielopass"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.cielo.cielopass"
        minSdk = 24
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 29
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "CIELO_CLIENT_ID", "\"${localProperties.getProperty("CIELO_CLIENT_ID", "")}\"")
        buildConfigField("String", "CIELO_ACCESS_TOKEN", "\"${localProperties.getProperty("CIELO_ACCESS_TOKEN", "")}\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
            }
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Core Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Core AndroidX & Activity Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose UI Components
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Lifecycle Support
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Jetpack Navigation 3 & Adaptive Layouts
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.material3.adaptive.navigation3)

    // Kotlinx Serialization Core & JSON
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Dependency Injection (Koin)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Networking (Retrofit & OkHttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DataStore & Protobuf
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)

    // Cryptography & Security
    implementation(libs.tink.android)
    implementation(libs.password4j)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.orgjson)

    // Testing (Unit & Instrumented)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
