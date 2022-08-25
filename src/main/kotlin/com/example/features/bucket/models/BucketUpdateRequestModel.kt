package com.example.features.bucket.models

import com.google.gson.annotations.SerializedName

data class BucketUpdateRequestModel(
    @SerializedName("bucket_id")
    val bucketId: Int,
    @SerializedName("bucket_password")
    val newPassword: String
)
