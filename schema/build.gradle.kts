import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.vanniktech.maven.publish") version "0.29.0"
}

kotlin {
    jvmToolchain(17)
}

val schemaVersion = properties["publish.versions.schema"] as String
val publishGroup = properties["publish.group"] as String

version = schemaVersion
group = publishGroup

dependencies {
    implementation(libs.wire.schema)
    implementation(libs.wire.kotlin.generator)
    implementation(kotlin("reflect"))
    implementation(projects.runtime)
}

mavenPublishing {

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(publishGroup, "schema", schemaVersion)

    pom {
        name = "Krpc Schema"
        description = "generate kotlin multiplatform code from .proto files base on wire"
        url.set("https://github.com/szkug/krpc")

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://github.com/szkug/krpc/blob/main/LICENSE"
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
            url.set("https://github.com/szkug/krpc/")
            connection.set("scm:git:git://github.com/szkug/krpc.git")
            developerConnection.set("scm:git:ssh://git@github.com/szkug/krpc.git")
        }
    }
}