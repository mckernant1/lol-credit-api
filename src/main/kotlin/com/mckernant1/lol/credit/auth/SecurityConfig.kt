package com.mckernant1.lol.credit.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth.anyRequest().authenticated()
            }
            .oauth2Login { oauth -> oauth.defaultSuccessUrl("/me", true) }
            .logout { logout -> logout.logoutSuccessUrl("/") }

        return http.build()
    }

}
