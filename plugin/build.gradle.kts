plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.1"
}

kotlin {
    jvmToolchain(17)
}

val schemaVersion = properties["publish.versions.schema"] as String
val pluginVersion = properties["publish.versions.plugin"] as String
val publishGroup = properties["publish.group"] as String

version = pluginVersion
group = publishGroup

dependencies {
    compileOnly(libs.gradle.plugin.kotlin)
    implementation(libs.gradle.plugin.wire)
    implementation(libs.wire.schema)
    implementation(projects.schema)
}

gradlePlugin {
    website.set("https://github.com/szkug/krpc")
    vcsUrl.set("https://github.com/szkug/krpc")

    plugins {
        register("KrpcPlugin") {
            id = publishGroup
            implementationClass = "KrpcPlugin"
            displayName = "Krpc Plugin"
            description = "generate kotlin multiplatform code from .proto files base on wire"
            tags.set(listOf("wire", "protobuf", "kotlin"))
        }
    }
}
