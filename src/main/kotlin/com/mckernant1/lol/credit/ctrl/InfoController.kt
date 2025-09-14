package com.mckernant1.lol.credit.ctrl

import com.mckernant1.lol.credit.db.models.Report
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/info")
class InfoController {

    data class GetReportTypes(
        val positiveAttributes: Map<String, String>,
        val negativeAttributes: Map<String, String>,
    )

    @GetMapping("/attributes")
    fun getReportTypes(): GetReportTypes {
        return GetReportTypes(
            Report.PositiveAttributes.entries.associate { it.name to it.description },
            Report.NegativeAttributes.entries.associate { it.name to it.description },
        )
    }

    @GetMapping("/roles")
    fun getRoles(): List<Report.Role> = Report.Role.entries

}
