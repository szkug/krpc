plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.wire.schema)
    implementation(libs.wire.kotlin.generator)
    implementation(projects.krpcRuntime)
    implementation(kotlin("reflect"))
}