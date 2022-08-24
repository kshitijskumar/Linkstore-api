package com.example.db.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object BucketEntity : Table() {

    val bucketId: Column<Int> = integer("bucket_id").uniqueIndex()
    val bucketPassword: Column<String> = varchar("bucket_password", 100)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(bucketId, name = "pk_${tableName}_bucketId")
}