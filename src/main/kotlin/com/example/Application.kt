package com.example

import com.example.db.DatabaseFactory
import com.example.features.bucket.routes.configureBucketRoutes
import com.example.features.bucket.service.BucketService
import io.ktor.server.application.*
import com.example.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureRouting()
    configureBucketRoutes(BucketService())
}
