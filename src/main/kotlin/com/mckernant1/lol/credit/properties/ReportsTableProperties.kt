package com.mckernant1.lol.credit.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "credit.aws.dynamodb.reports")
class ReportsTableProperties {
    lateinit var tableName: String
}
