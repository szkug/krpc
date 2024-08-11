import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.vanniktech.maven.publish") version "0.29.0"
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

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

val versionSuffix = properties["publish.versioins.suffix"]
val runtimeVersion = properties["publish.versioins.runtime"] as String

mavenPublishing {

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("org.szkug.krpc", "runtime", "$runtimeVersion-$versionSuffix")

    pom {
        name = "Krpc Runtime"
        description = "generate kotlin multiplatform code from .proto files base on wire"
        url.set("https://github.com/KaterinaPetrova/mpp-sample-lib")

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://github.com/szkug/krpc-plugin/blob/main/LICENSE"
            }
        }

        developers {
            developer {
                id = "korilin"
                name = "Kori"
                email = "korilin.dev@gmail.com"
            }
        }

        scm {
            url.set("https://github.com/szkug/krpc-plugin/")
            connection.set("scm:git:git://github.com/szkug/krpc-plugin.git")
            developerConnection.set("scm:git:ssh://git@github.com/szkug/krpc-plugin.git")
        }
    }

}