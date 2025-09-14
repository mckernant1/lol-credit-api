package com.mckernant1.lol.credit.db

import com.mckernant1.lol.credit.db.models.Report
import com.mckernant1.lol.credit.db.models.ReportEntity
import com.mckernant1.lol.credit.properties.ReportsTableProperties
import org.springframework.stereotype.Service
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import java.time.Instant

@Service
class ReportService(
    reportsTableProperties: ReportsTableProperties,
    ddbEnhancedClient: DynamoDbEnhancedClient,
    private val instantConverter: AttributeConverter<Instant>
) {

    private val reportTable: DynamoDbTable<ReportEntity> = ddbEnhancedClient.table(
        reportsTableProperties.tableName,
        TableSchema.fromBean(
            ReportEntity::class.java
        )
    )

    private val reportedPlayerIdIndex: DynamoDbIndex<ReportEntity> = reportTable.index(ReportEntity.REPORTED_USER_INDEX)

    private val reporterPlayerIdIndex: DynamoDbIndex<ReportEntity> = reportTable.index(ReportEntity.REPORTER_USER_INDEX)

    fun createReport(report: Report) {
        reportTable.putItem(report.toReportEntity())
    }

    fun getReport(reportId: String): Report? {
        return reportTable.getItem(
            Key.builder()
                .partitionValue(reportId)
                .build()
        )?.toReport()
    }

    fun getReportsForRiotId(
        riotId: String,
        after: Instant
    ): List<Report> {

        val reports = reportedPlayerIdIndex.query { builder ->

            builder.queryConditional(
                QueryConditional.sortGreaterThan {
                    it.partitionValue(riotId)
                    it.sortValue(instantConverter.transformFrom(after))
                }
            )
            builder.scanIndexForward(false)
        }

        return reports
            .asSequence()
            .flatMap { it.items() }
            .map { it.toReport() }
            .toList()
    }

    fun getReportsForGoogleId(
        googleId: String,
        after: Instant
    ): List<Report> {

        val reports = reporterPlayerIdIndex.query { builder ->

            builder.queryConditional(
                QueryConditional.sortGreaterThan {
                    it.partitionValue(googleId)
                    it.sortValue(instantConverter.transformFrom(after))
                }
            )
            builder.scanIndexForward(false)
        }

        return reports
            .asSequence()
            .flatMap { it.items() }
            .map { it.toReport() }
            .toList()
    }

}
