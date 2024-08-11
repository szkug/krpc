plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.1"
}

kotlin {
    jvmToolchain(17)
}

val versionSuffix = properties["publish.versioins.suffix"] as String
val schemaVersion = properties["publish.versioins.schema"] as String
val pluginVersion = properties["publish.versioins.plugin"] as String

version = "$pluginVersion-$versionSuffix"

dependencies {
    compileOnly(libs.gradle.plugin.kotlin)
    implementation(libs.gradle.plugin.wire)
    implementation(libs.wire.schema)
    implementation(projects.schema)

    implementation("org.szkug.krpc:schema:$schemaVersion-$versionSuffix")
}

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
