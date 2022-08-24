package com.example.features.bucket.exceptions

import java.lang.Exception

data class NoBucketFoundException(
    val bucketId: Int,
    val msg: String
): Exception(msg)