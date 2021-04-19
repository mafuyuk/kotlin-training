package com.example.oauth2.clientcredentials.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class WebBasicAuthSecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // homeはアクセスできる
        http.authorizeRequests()
            .antMatchers("/home")
            .permitAll()

        // userリソースは制限をかけている
        http.authorizeRequests()
            .mvcMatchers("/user/**").hasAuthority("SCOPE_user:read")
            .anyRequest().authenticated()

        http.oauth2ResourceServer()
            .jwt()
    }
}