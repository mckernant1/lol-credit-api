package com.mckernant1.lol.credit.db.models

import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.InstantAsStringAttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey
import java.time.Instant


@DynamoDbBean
open class ReportEntity(

    @get:DynamoDbPartitionKey
    var reportId: String? = null,

    @get:DynamoDbSecondaryPartitionKey(indexNames = [REPORTED_USER_INDEX])
    var reportedUserRiotId: String? = null,

    @get:DynamoDbSecondaryPartitionKey(indexNames = [REPORTER_USER_INDEX])
    var reporterUserGoogleId: String? = null,

    @get:DynamoDbSecondarySortKey(indexNames = [REPORTER_USER_INDEX, REPORTED_USER_INDEX])
    var gameTimestamp: Instant? = null,

    var role: Report.Role? = null,
    var champion: String? = null,
    var createdTimestamp: Instant? = null,
    var updatedTimestamp: Instant? = null,
    var negativeAttributes: List<Report.NegativeAttributes>? = emptyList(),
    var positiveAttributes: List<Report.PositiveAttributes>? = emptyList()
) {
    companion object {
        const val REPORTED_USER_INDEX = "reported-user-index"
        const val REPORTER_USER_INDEX = "reporter-user-index"
    }

    fun toReport(): Report {
        return Report(
            reportId = reportId!!,
            reportedUserRiotId = reportedUserRiotId!!,
            reporterUserGoogleId = reporterUserGoogleId!!,
            gameTimestamp = gameTimestamp!!,
            role = role!!,
            champion = champion!!,
            createdTimestamp = createdTimestamp!!,
            updatedTimestamp = updatedTimestamp,
            negativeAttributes = negativeAttributes ?: emptyList(),
            positiveAttributes = positiveAttributes ?: emptyList()
        )
    }
}


data class Report(
    val reportId: String,
    val reportedUserRiotId: String,
    val reporterUserGoogleId: String,
    val role: Role,
    val champion: String,
    val gameTimestamp: Instant,
    val createdTimestamp: Instant,
    var updatedTimestamp: Instant?,
    var negativeAttributes: List<NegativeAttributes>,
    var positiveAttributes: List<PositiveAttributes>
) {

    fun toReportEntity(): ReportEntity = ReportEntity(
        reportId = reportId,
        reportedUserRiotId = reportedUserRiotId,
        reporterUserGoogleId = reporterUserGoogleId,
        role = role,
        champion = champion,
        createdTimestamp = createdTimestamp,
        updatedTimestamp = updatedTimestamp,
        negativeAttributes = negativeAttributes,
        gameTimestamp = gameTimestamp,
        positiveAttributes = positiveAttributes
    )

    enum class Role {
        Top,
        Jungle,
        Mid,
        Bottom,
        Support
    }

    enum class NegativeAttributes(val description: String) {
        YAPPER("Talks too much"),
        RACISM("Racism or slurs"),

        UNAWARE("Doesnt look at map"),
        GETS_CAUGHT("Finds themselves 1 vs Many for no trade consistently"),
        SMURFING("Player displayed skill level far above normal for the current rank"),
        FEEDING("Player displayed skill level far below normal for the current rank"),
        TROLLING("Player purposefully dying, assisting enemy team, or playing in incorrect lane"),
        MISSES_SKILL_SHOTS("Misses critical abilities consistently"),

        TROLL_BUILDS("Builds items that are not considered good in the current meta or situation"),
        TROLL_PICKS("Picks champions that harm team outcomes in the current meta or situation"),

    }

    enum class PositiveAttributes(val description: String) {
        POSITIVE_VIBES("Contributes to chat positively"),
        SILENT_HERO("Ignored toxic chat messages to still perform well"),

        HITS_SKILL_SHOTS("Hits critical abilities consistently"),
        GOT_JUKES("Dodges skillshots consistently or critically"),

        MAP_AWARE("Doesnt get caught. Wards well. Looks at map."),
        FOLLOWS_UP("Follows up on teammates playmaking"),
        GOOD_ROTATIONS("Rotates around the map to make plays with teammates"),

        GOOD_BUILDS("Itemized correctly against opponents"),
        GOOD_PICK("Utilized Counter picks effectively, or picked a good champion for the situation"),
        WEAK_SIDE_HERO("Played well into a bad matchup and still contributed to the game"),
    }
}
