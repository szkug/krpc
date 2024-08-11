plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.gradle.plugin.kotlin)
    implementation(libs.gradle.plugin.wire)
    implementation(libs.wire.schema)
    implementation(projects.schema)
}

gradlePlugin {
    plugins {
        register("KrpcPlugin") {
            id = "org.szkug.krpc"
            implementationClass = "KrpcPlugin"
        }
    }
}