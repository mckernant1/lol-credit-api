package com.mckernant1.lol.credit.db.models

import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.InstantAsStringAttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.time.Instant

@DynamoDbBean
class UserEntity(
    @get:DynamoDbPartitionKey
    var userId: String? = null,

    var accountCreatedTimestamp: Instant? = null,

    var preferredSearchDays: Int? = null
) {
    fun toUser(): User {
        return User(
            userId!!,
            accountCreatedTimestamp!!,
            preferredSearchDays!!
        )
    }
}

data class User(
    val userId: String,
    val accountCreatedTimestamp: Instant,
    var preferredSearchDays: Int = 30
) {
    fun toUserEntity(): UserEntity {
        return UserEntity(
            userId,
            accountCreatedTimestamp,
            preferredSearchDays
        )
    }
}
