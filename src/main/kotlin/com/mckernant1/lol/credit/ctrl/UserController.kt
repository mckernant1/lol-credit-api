package com.mckernant1.lol.credit.ctrl

import com.mckernant1.commons.logging.Slf4j.logger
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    companion object {
        private val logger = logger()
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun me(@AuthenticationPrincipal(errorOnInvalidType = true) auth: OidcUser?): Map<String, Any?> {
        if (auth == null) {
            logger.info("No auth provided '$auth'")
            return mapOf("error" to "Authentication is required")
        }

        return mapOf(
            "authClass" to auth.javaClass.simpleName,
            "name" to auth.name,
            "sub" to auth.idToken
        )
    }
}
