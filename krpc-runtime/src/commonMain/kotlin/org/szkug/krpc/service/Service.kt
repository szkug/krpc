package org.szkug.krpc.service


interface Service: com.squareup.wire.Service

interface ServiceFactory {
    fun create(call: Call)
}