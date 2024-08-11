package org.szkug.krpc.plugin

import com.squareup.wire.schema.SchemaHandler

class KrpcSchemaHandlerFactory private constructor(
    private val role: KrpcRole
): SchemaHandler.Factory {
    @Deprecated("Wire does not call this method anymore. Implement the other 'create' method to receive the payload associated with the schema handler.")
    override fun create(): SchemaHandler {
        TODO("Not yet implemented")
    }

    override fun create(
        includes: List<String>,
        excludes: List<String>,
        exclusive: Boolean,
        outDirectory: String,
        options: Map<String, String>
    ): SchemaHandler {
        return KrpcSchemaHandler(outDirectory, role)
    }

    companion object {

        fun client() = KrpcSchemaHandlerFactory(KrpcRole.Client)
        fun server() = KrpcSchemaHandlerFactory(KrpcRole.Server)
    }
}