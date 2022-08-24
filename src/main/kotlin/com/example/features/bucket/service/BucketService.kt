package com.example.features.bucket.service

import com.example.db.DatabaseFactory.dbQuery
import com.example.db.entities.BucketEntity
import com.example.features.bucket.exceptions.NoBucketFoundException
import com.example.features.bucket.models.BucketModel
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class BucketService {

    suspend fun doesBucketAlreadyExists(bucketId: Int): Boolean = dbQuery {
        val resultRow = BucketEntity.selectAll().find { it[BucketEntity.bucketId] == bucketId }
        resultRow != null
    }

    suspend fun createAnEmptyBucket(): BucketModel? = dbQuery {
        val currentLargestGroupNumberResultRow = BucketEntity.selectAll().sortedBy { it[BucketEntity.bucketId] }
            .lastOrNull()
        val currentLargestGroupNumber = currentLargestGroupNumberResultRow?.let {
            it[BucketEntity.bucketId]
        } ?: 1000

        val newBucketId = currentLargestGroupNumber.plus(1)

        val insertStatement = BucketEntity.insert {
            it[BucketEntity.bucketId] = newBucketId
            it[BucketEntity.bucketPassword] = newBucketId.toString()
        }
        insertStatement.resultedValues?.singleOrNull()?.let { resultRowToBucketModel(it) }
    }

    @Throws(NoBucketFoundException::class)
    suspend fun updateBucketPassword(bucketId: Int, newBucketPassword: String) = dbQuery {
        val updateCount = BucketEntity.update(
            where = { BucketEntity.bucketId eq bucketId },
            body = {
                it[BucketEntity.bucketPassword] = newBucketPassword
            }
        )
        if (updateCount <= 0) {
            throw NoBucketFoundException(
                bucketId = bucketId,
                msg = "No bucket found for given bucket id"
            )
        }
    }


    private fun resultRowToBucketModel(row: ResultRow): BucketModel {
        return BucketModel(
            bucketId = row[BucketEntity.bucketId],
            bucketPassword = row[BucketEntity.bucketPassword]
        )
    }

}