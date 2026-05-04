import java.util.Properties

private fun String.escapeForBuildConfigString(): String =
    replace("\\", "\\\\").replace("\"", "\\\"")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.warehouse_accounting_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.warehouse_accounting_app"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    fun loadLocalProperties(): Properties {
        val props = Properties()
        rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { props.load(it) }
        return props
    }

    buildTypes {
        debug {
            manifestPlaceholders["usesCleartextTraffic"] = "true"
            val props = loadLocalProperties()
            val apiBase = props.getProperty("api.base.url", "http://10.0.2.2:8080").trim()
            buildConfigField("String", "API_BASE_URL", "\"${apiBase.escapeForBuildConfigString()}\"")
        }
        release {
            manifestPlaceholders["usesCleartextTraffic"] = "false"
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val props = loadLocalProperties()
            val apiBase = props.getProperty("api.base.url", "").trim()
            buildConfigField("String", "API_BASE_URL", "\"${apiBase.escapeForBuildConfigString()}\"")
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.compose.material.icons.extended)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

val validateReleaseApiBaseUrl by tasks.registering {
    doLast {
        val props = Properties()
        rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { props.load(it) }
        val apiBase = props.getProperty("api.base.url", "").trim()
        require(apiBase.isNotEmpty()) {
            "Release build: set api.base.url in local.properties to your HTTPS API base URL " +
                "(cleartext HTTP is disabled for release)."
        }
        require(apiBase.startsWith("https://", ignoreCase = true)) {
            "Release build: api.base.url must use HTTPS (cleartext is off). Current value: $apiBase"
        }
    }
}

afterEvaluate {
    tasks.named("preReleaseBuild").configure { dependsOn(validateReleaseApiBaseUrl) }
}
