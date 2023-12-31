package org.szkug.krpc.plugin

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.wire.schema.*
import com.squareup.wire.schema.internal.javaPackage
import okio.ByteString

private val BYTE_STRING = ByteString::class.asClassName()

class KrpcServiceGenerator(
    schema: Schema,
    private val profile: Profile,
) {

    private val typeToKotlinName: Map<ProtoType, TypeName>

    private val ProtoType.typeName: TypeName
        get() {
            return if (isMap) {
                Map::class.asTypeName()
                    .parameterizedBy(keyType!!.typeName, valueType!!.typeName)
            } else {
                profile.kotlinTarget(this) ?: typeToKotlinName.getValue(this)
            }
        }

    private val Service.interfaceName: ClassName
        get() {
            val typeName = type.typeName as ClassName
            val simpleName = buildString {
                append(typeName.simpleName)
            }
            return typeName.peerClass(simpleName)
        }

    private val ClassName.implementName: ClassName
        get() {
            val implName = buildString {
                append("Krpc")
                append(simpleName)
            }
            return peerClass(implName)
        }

    private val rpcParamName = "request"

    fun generateServiceImpl(service: Service, role: KrpcRole): Pair<ClassName, TypeSpec> {
        val interfaceName = service.interfaceName
        val name = interfaceName.implementName
        val serviceName = interfaceName.canonicalName

        fun createClientProxyFunc(rpc: Rpc): FunSpec {
            return FunSpec.builder(rpc.name).apply {
                addModifiers(KModifier.OVERRIDE)
                addModifiers(KModifier.SUSPEND)

                val req = rpc.requestType!!
                val resp = rpc.responseType!!

                addParameter(rpcParamName, req.typeName)
                returns(resp.typeName)

                addStatement("val bytes = ${rpcParamName}.encodeByteString()")
                addStatement("val response = call(%S, %S, bytes)", serviceName, rpc.name)
                addStatement("return ${resp.simpleName}.ADAPTER.decode(response)")
            }.build()
        }

        fun createServerMapFunc(): FunSpec {
            return FunSpec.builder("call").apply {
                addModifiers(KModifier.FINAL)
                addModifiers(KModifier.SUSPEND)

                addParameter("request", BYTE_STRING)
                addParameter("name", STRING)
                returns(BYTE_STRING)

                beginControlFlow("val response = when(name)")
                for (rpc in service.rpcs) {

                    val functionName = rpc.name
                    val decode = rpc.requestType!!.typeName.toString() + ".ADAPTER.decode(request)"

                    addStatement("%S -> $functionName($decode)", rpc.name)
                }
                val exception = IllegalStateException::class.asClassName()
                addStatement("else -> throw ${exception.packageName}.${exception.simpleName}(\"Illegal function name \$name\")")
                endControlFlow()

                addStatement("return response.encodeByteString()")
            }.build()
        }

        val builder = TypeSpec.classBuilder(name).apply {
            addSuperinterface(interfaceName)

            if (role == KrpcRole.Server) {
                addModifiers(KModifier.ABSTRACT)
                createServerMapFunc().also { func -> addFunction(func) }
                addProperty(PropertySpec.builder("serverName", STRING).initializer("%S", serviceName).build())
            } else {

                val callType = LambdaTypeName.get(
                    parameters = arrayOf(STRING, STRING, BYTE_STRING), // serviceName, functionName, request
                    returnType = BYTE_STRING
                ).copy(suspending = true)
                val constructor = FunSpec.constructorBuilder().addParameter("call", callType).build()
                primaryConstructor(constructor)

                val callProperty = PropertySpec.builder("call", callType)
                    .initializer("call")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
                addProperty(callProperty)

                for (rpc in service.rpcs) {
                    createClientProxyFunc(rpc).also { func -> addFunction(func) }
                }
            }
        }

        return name to builder.build()
    }

    fun generateServiceInterface(service: Service): Pair<ClassName, TypeSpec> {

        val name = service.interfaceName

        val builder = TypeSpec.interfaceBuilder(name).apply {
            addSuperinterface(com.squareup.wire.Service::class)

            if (service.documentation.isNotBlank()) {
                addKdoc("%L\n", service.documentation.sanitizeKdoc())
            }

            for (rpc in service.rpcs) {
                FunSpec.builder(rpc.name).apply {
                    if (rpc.documentation.isNotBlank()) {
                        addKdoc("%L\n", rpc.documentation.sanitizeKdoc())
                    }
                    addModifiers(KModifier.SUSPEND)
                    addModifiers(KModifier.ABSTRACT)

                    val req = rpc.requestType!!
                    val resp = rpc.responseType!!

                    addParameter(ParameterSpec.builder(rpcParamName, req.typeName).build())
                    returns(resp.typeName)
                }.build().also { addFunction(it) }
            }
        }

        return name to builder.build()
    }


    private fun String.sanitizeKdoc(): String {
        return this
            // Remove trailing whitespace on each line.
            .replace("[^\\S\n]+\n".toRegex(), "\n")
            .replace("\\s+$".toRegex(), "")
            .replace("\\*/".toRegex(), "&#42;/")
            .replace("/\\*".toRegex(), "/&#42;")
            .replace("""[""", """\[""")
            .replace("""]""", """\]""")
    }

    init {
        typeToKotlinName = mutableMapOf()

        fun putAll(kotlinPackage: String, enclosingClassName: ClassName?, types: List<Type>) {
            for (type in types) {
                val className = enclosingClassName?.nestedClass(type.type.simpleName)
                    ?: ClassName(kotlinPackage, type.type.simpleName)
                typeToKotlinName[type.type] = className
                putAll(kotlinPackage, className, type.nestedTypes)
            }
        }

        for (protoFile in schema.protoFiles) {
            val kotlinPackage = javaPackage(protoFile)
            putAll(kotlinPackage, null, protoFile.types)

            for (service in protoFile.services) {
                val className = ClassName(kotlinPackage, service.type.simpleName)
                typeToKotlinName[service.type] = className
            }
        }


        typeToKotlinName.putAll(BUILT_IN_TYPES)
    }
}


private val BUILT_IN_TYPES = mapOf(
    ProtoType.BOOL to BOOLEAN,
    ProtoType.BYTES to ByteString::class.asClassName(),
    ProtoType.DOUBLE to DOUBLE,
    ProtoType.FLOAT to FLOAT,
    ProtoType.FIXED32 to INT,
    ProtoType.FIXED64 to LONG,
    ProtoType.INT32 to INT,
    ProtoType.INT64 to LONG,
    ProtoType.SFIXED32 to INT,
    ProtoType.SFIXED64 to LONG,
    ProtoType.SINT32 to INT,
    ProtoType.SINT64 to LONG,
    ProtoType.STRING to String::class.asClassName(),
    ProtoType.UINT32 to INT,
    ProtoType.UINT64 to LONG,
    ProtoType.ANY to ClassName("com.squareup.wire", "AnyMessage"),
    ProtoType.DURATION to ClassName("com.squareup.wire", "Duration"),
    ProtoType.TIMESTAMP to ClassName("com.squareup.wire", "Instant"),
    ProtoType.EMPTY to ClassName("kotlin", "Unit"),
    ProtoType.STRUCT_MAP to ClassName("kotlin.collections", "Map")
        .parameterizedBy(ClassName("kotlin", "String"), STAR).copy(nullable = true),
    ProtoType.STRUCT_VALUE to ClassName("kotlin", "Any").copy(nullable = true),
    ProtoType.STRUCT_NULL to ClassName("kotlin", "Nothing").copy(nullable = true),
    ProtoType.STRUCT_LIST to ClassName("kotlin.collections", "List")
        .parameterizedBy(STAR).copy(nullable = true),
    ProtoType.DOUBLE_VALUE to DOUBLE.copy(nullable = true),
    ProtoType.FLOAT_VALUE to FLOAT.copy(nullable = true),
    ProtoType.INT64_VALUE to LONG.copy(nullable = true),
    ProtoType.UINT64_VALUE to LONG.copy(nullable = true),
    ProtoType.INT32_VALUE to INT.copy(nullable = true),
    ProtoType.UINT32_VALUE to INT.copy(nullable = true),
    ProtoType.BOOL_VALUE to BOOLEAN.copy(nullable = true),
    ProtoType.STRING_VALUE to String::class.asClassName().copy(nullable = true),
    ProtoType.BYTES_VALUE to ByteString::class.asClassName().copy(nullable = true),
)