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
        http.authorizeRequests()
            .antMatchers("/home").permitAll() // homeはアクセスできる
            .anyRequest().hasAuthority("read") // それ他はscope readが付与されているアクセストークンを使用しないといけない
//        http.oauth2ResourceServer()
//            .jwt()
    }
}