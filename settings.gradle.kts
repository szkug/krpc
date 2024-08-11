pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// gradle feature
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("schema")
include("runtime")
include("plugin")