package com.mckernant1.lol.credit.ctrl

import com.mckernant1.commons.logging.Slf4j.logger
import com.mckernant1.lol.credit.db.ReportService
import com.mckernant1.lol.credit.db.models.Report
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.UUID


@RestController("/api/reported")
class ReportedUserController(
    private val reportService: ReportService,
) {

    companion object {
        private val logger = logger()
    }

    data class ReportsOnUserResponse(
        val positiveRoleReportMap: Map<Report.Role, Map<Report.PositiveAttributes, Int>>,
        val negativeRoleReportMap: Map<Report.Role, Map<Report.NegativeAttributes, Int>>
    )

    @GetMapping("/{riotId}")
    fun getReportsOnUser(
        @PathVariable("riotId") riotId: String,
        @RequestParam("start") start: Instant,
    ): ReportsOnUserResponse {
        val reports = reportService.getReportsForRiotId(riotId, start)

        val reportsByRole = reports.groupBy { it.role }

        val positiveReportsByRole = reportsByRole
            .mapValues { (_, reports) ->
                reports.flatMap { it.positiveAttributes }.groupingBy { it }.eachCount()
            }

        val negativeReportsByRole = reportsByRole
            .mapValues { (_, reports) ->
                reports.flatMap { it.negativeAttributes }.groupingBy { it }.eachCount()
            }

        return ReportsOnUserResponse(
            positiveReportsByRole,
            negativeReportsByRole
        )
    }

    data class PostReport(
        @field:NotNull(message = "role is required")
        val role: Report.Role,
        @field:NotBlank(message = "champion is required")
        val champion: String,
        @field:NotNull(message = "gameTimestamp is required")
        val gameTimestamp: Instant,
        val negativeAttributes: List<Report.NegativeAttributes> = emptyList(),
        val positiveAttributes: List<Report.PositiveAttributes> = emptyList()
    )

    @PostMapping("/{riotId}/submit")
    fun postReportOnUser(
        @AuthenticationPrincipal(errorOnInvalidType = true) auth: OidcUser,
        @PathVariable("riotId") riotId: String,
        @RequestBody @Valid postReport: PostReport,
    ) {

        if (postReport.positiveAttributes.isEmpty() && postReport.negativeAttributes.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Negative or positive attributes are required")
        }

        val report = Report(
            reportId = UUID.randomUUID().toString(),
            reporterUserGoogleId = auth.subject,
            reportedUserRiotId = riotId,
            createdTimestamp = Instant.now(),
            champion = postReport.champion,
            gameTimestamp = postReport.gameTimestamp,
            negativeAttributes = postReport.negativeAttributes,
            positiveAttributes = postReport.positiveAttributes,
            role = postReport.role,
            updatedTimestamp = null,
        )

        reportService.createReport(report)
    }

    data class PutReport(
        @field:NotBlank(message = "reportId is required")
        val reportId: String,
        val negativeAttributes: List<Report.NegativeAttributes> = emptyList(),
        val positiveAttributes: List<Report.PositiveAttributes> = emptyList()
    )

    @PutMapping("/{riotId}/update")
    fun updateReportOnUser(
        @AuthenticationPrincipal(errorOnInvalidType = true) auth: OidcUser,
        @PathVariable("riotId") riotId: String,
        @RequestBody @Valid putReport: PutReport,
    ) {
        if (putReport.positiveAttributes.isEmpty() && putReport.negativeAttributes.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Negative or positive attributes are required")
        }

        val oldReport = reportService.getReport(putReport.reportId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Report not found")

        if (riotId != oldReport.reportedUserRiotId) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "RiotId $riotId does not match id of existing report")
        }

        if (auth.name != oldReport.reporterUserGoogleId) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Auth id does not match google id of existing report")
        }

        oldReport.positiveAttributes = putReport.positiveAttributes
        oldReport.negativeAttributes = putReport.negativeAttributes
        oldReport.updatedTimestamp = Instant.now()

        reportService.createReport(oldReport)
    }

}
