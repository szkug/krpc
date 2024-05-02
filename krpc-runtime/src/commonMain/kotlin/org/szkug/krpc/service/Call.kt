package org.szkug.krpc.service

interface Call {

    operator fun invoke(serviceName: String, functionName: String, requestData: ByteArray)
}