import com.squareup.wire.gradle.WireExtension
import com.squareup.wire.gradle.WirePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.szkug.krpc.plugin.KrpcSchemaHandlerFactory

/**
 * @PluginId cn.szkug.krpc
 */
class KrpcPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        configPlugin()
        configExtension()
    }

    private fun Project.configPlugin() = with(pluginManager) {
        apply(WirePlugin::class.java)
    }

    private fun Project.configExtension() = with(extensions) {

        configure<WireExtension> {
            custom {
                schemaHandlerFactory = KrpcSchemaHandlerFactory.client()
                out = "${layout.buildDirectory.asFile.get()}/generated"
            }
        }

        configure<KotlinMultiplatformExtension> {
            val lastRuntimeVersion = properties["publish.versions.runtime"] as String
            val publishGroup = properties["publish.group"] as String

            sourceSets.apply {
                commonMain.dependencies {
                    implementation("$publishGroup:runtime:$lastRuntimeVersion")
                }
            }
        }
    }
}