package com.example.features.bucket.routes

import com.example.features.bucket.exceptions.NoBucketFoundException
import com.example.features.bucket.models.BucketUpdateRequestModel
import com.example.features.bucket.service.BucketService
import com.example.utils.GenericLinksStoreErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.Exception

fun Application.configureBucketRoutes(
    bucketService: BucketService
) {
    routing {
        route(path = "/buckets") {
            route(path = "/v1") {
                post(path = "/create-bucket") {
                    val createdBucket = bucketService.createAnEmptyBucket()
                    if (createdBucket != null) {
                        call.respond(
                            status = HttpStatusCode.Created,
                            createdBucket
                        )
                    } else {
                        call.respond(
                            status = HttpStatusCode.InternalServerError,
                            message = GenericLinksStoreErrorResponse(
                                errorCode = HttpStatusCode.InternalServerError.value,
                                errorMsg = "Something went wrong"
                            )
                        )
                    }
                }

                get(path = "/bucket-status/{bucket_id}") {
                    val bucketId = call.parameters["bucket_id"]?.toIntOrNull()
                    if (bucketId == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = GenericLinksStoreErrorResponse(HttpStatusCode.BadRequest.value, "invalid bucketId")
                        )
                    } else {
                        val doesBucketExist = bucketService.doesBucketAlreadyExists(bucketId)
                        val bucketStatus = if (doesBucketExist) {
                            "bucket exists"
                        } else {
                            "bucket doesn't exists"
                        }

                        call.respond(
                            status = HttpStatusCode.OK,
                            message = bucketStatus
                        )
                    }
                }

                put(path = "update-bucket") {
                    try {
                        val requestBody = call.receive<BucketUpdateRequestModel>()
                        bucketService.updateBucketPassword(
                            bucketId = requestBody.bucketId,
                            newBucketPassword = requestBody.newPassword
                        )
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = "password updated"
                        )
                    } catch (e: NoBucketFoundException) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = GenericLinksStoreErrorResponse(
                                errorCode = HttpStatusCode.BadRequest.value,
                                errorMsg = e.msg
                            )
                        )
                    } catch (e: Exception) {
                        call.respond(
                            status = HttpStatusCode.InternalServerError,
                            message = GenericLinksStoreErrorResponse(
                                errorCode = HttpStatusCode.InternalServerError.value,
                                errorMsg = "Something went wrong: ${e.message}"
                            )
                        )
                    }

                }

            }
        }
    }
}