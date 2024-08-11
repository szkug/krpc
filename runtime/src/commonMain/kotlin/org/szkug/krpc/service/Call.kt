package org.szkug.krpc.service

interface Call {

    suspend operator fun invoke(serviceName: String, functionName: String, requestData: ByteArray): ByteArray
}