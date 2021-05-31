package com.oauth2.authorizationcode.resourceserver.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class WebBasicAuthSecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.cors()
            .and()
                .authorizeRequests()
                    .antMatchers("/home").hasAuthority("SCOPE_email")
            .and()
                .oauth2ResourceServer()
                    .jwt()
    }
}