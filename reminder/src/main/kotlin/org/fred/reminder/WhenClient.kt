package org.fred.reminder

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import srv.`when`.WhenGrpc
import srv.`when`.WhenRequest
import srv.`when`.WhenResponse
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger


class WhenClient {
    private val logger = Logger.getLogger(WhenClient::class.java.name)

    private lateinit var channel: ManagedChannel
    private lateinit var blockingStub:WhenGrpc.WhenBlockingStub

    constructor(host: String, port: Int) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build()
        blockingStub = WhenGrpc.newBlockingStub(channel)
    }
    fun shutdown() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    fun greet(name: String):String {
        logger.info("Will try to greet $name ...")
        val request = WhenRequest.newBuilder().setName(name).build()
        val response: WhenResponse
        try {
            response = blockingStub.parse(request)
        } catch (e: StatusRuntimeException) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.status)
            return "RPC failed: ${e.status}"
        }

        logger.info("Greeting: " + response.getMessage())
        return response.getMessage()
    }
}
