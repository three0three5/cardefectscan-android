import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
}

android {
    namespace = "ru.hse.cardefectscan"
    compileSdk = 35
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "ru.hse.cardefectscan"
        minSdk = 29
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    sourceSets {
        getByName("main").java.srcDirs("${layout.buildDirectory.get()}/generated/src")
    }

    val propertiesFile = rootProject.file("application.properties")
    val properties = Properties()
    properties.load(FileInputStream(propertiesFile))

    defaultConfig {
        buildConfigField(
            "String",
            "SERVER_BASE_URL",
            properties["SERVER_BASE_URL"].toString(),
        )
    }
}

dependencies {
    implementation(libs.hilt.navigation)
    implementation(libs.dagger)
    implementation(libs.hilt.android)

    kapt(libs.hilt.compiler)

    implementation(libs.okhttp3)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    testImplementation(libs.hilt.android.test)
    kaptTest(libs.hilt.compiler)

    androidTestImplementation(libs.hilt.android.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    kaptAndroidTest(libs.hilt.compiler)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}

openApiGenerate {
    generatorName.set("kotlin")
    library.set("jvm-okhttp4")
    inputSpec.set("$rootDir/cardefectscan.yaml")
    outputDir.set("${layout.buildDirectory.get()}/generated")
    packageName.set("ru.hse.generated")
    generateApiTests.set(false)
    generateModelTests.set(false)
}

tasks {
    preBuild.configure {
        dependsOn("openApiGenerate")
    }
}
