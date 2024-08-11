plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

kotlin {
    jvmToolchain(17)
}

val versionSuffix = properties["publish.versioins.suffix"] as String

val runtimeVersion = properties["publish.versioins.runtime"] as String
val schemaVersion = properties["publish.versioins.schema"] as String

dependencies {
    implementation(libs.wire.schema)
    implementation(libs.wire.kotlin.generator)
    implementation(kotlin("reflect"))
    implementation("org.szkug.krpc:runtime:$runtimeVersion-$versionSuffix")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.szkug.krpc"
            artifactId = "schema"
            version = "$schemaVersion-$versionSuffix"
        }
    }
}