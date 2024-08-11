plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.1"
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

group = "org.szkug.krpc"
version = "1.0.0-unk"

gradlePlugin {
    website.set("https://github.com/szkug/krpc")
    vcsUrl.set("https://github.com/szkug/krpc")

    plugins {
        register("KrpcPlugin") {
            id = "org.szkug.krpc"
            implementationClass = "KrpcPlugin"
            displayName = "Krpc Plugin"
            description = "generate kotlin multiplatform code from .proto files base on wire"
            tags.set(listOf("wire", "protobuf", "kotlin"))
        }
    }
}
