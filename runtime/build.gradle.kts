plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm() // MARK: jvm target could be desktop & android library
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        iosMain.dependencies {
        }
        jvmMain.dependencies {
        }
        commonMain.dependencies {
            api(libs.wire.runtime)
        }
    }
}