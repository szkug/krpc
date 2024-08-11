package org.szkug.krpc.service


interface Service: com.squareup.wire.Service

interface ServiceFactory<S: Service> {
    fun create(call: Call): S
}