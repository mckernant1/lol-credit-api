package com.mckernant1.lol.credit.ctrl

import com.mckernant1.lol.credit.db.models.Report
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api/info")
class InfoController {

    data class GetReportTypes(
        val positiveAttributes: List<Report.PositiveAttributes>,
        val negativeAttributes: List<Report.NegativeAttributes>,
    )

    @GetMapping("/attributes")
    fun getReportTypes(): GetReportTypes {
        return GetReportTypes(
            Report.PositiveAttributes.entries,
            Report.NegativeAttributes.entries
        )
    }

    @GetMapping("/roles")
    fun getRoles(): List<Report.Role> = Report.Role.entries

}
