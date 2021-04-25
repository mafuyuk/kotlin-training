package com.oauth2.clientcredentials.resourceserver.configuration

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
                    .antMatchers("/home").permitAll() // homeはアクセスできる
                    .antMatchers("/users").hasAuthority("SCOPE_hoge") // usersはscopeが存在しないのでアクセスできない
                    .anyRequest().hasAuthority("SCOPE_read") // その他はscope readが付与されているアクセストークンを使用しないといけない
            .and()
                .oauth2ResourceServer()
                    .jwt()
    }
}