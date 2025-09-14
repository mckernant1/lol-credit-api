package com.mckernant1.lol.credit.db

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.InstantAsStringAttributeConverter
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Instant

@Configuration
class DynamoDbConfig {


    @Bean
    fun ddbClient(): DynamoDbClient = DynamoDbClient.create()

    @Bean
    fun ddbEnhanced(ddbClient: DynamoDbClient): DynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
        .dynamoDbClient(ddbClient)
        .build()

    @Bean
    fun instantToAttributeValueConverter(): AttributeConverter<Instant> = InstantAsStringAttributeConverter.create()


}
