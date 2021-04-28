package com.oauth2.clientcredentials.resourceserver.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import javax.crypto.spec.SecretKeySpec


@Configuration
@EnableWebSecurity
class WebBasicAuthSecurityConfiguration : WebSecurityConfigurerAdapter() {

    val logger: Logger = LoggerFactory.getLogger(WebBasicAuthSecurityConfiguration::class.java)

    @Value("\${security.signing-key}")
    val signingKey = ""

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
                    .jwt().decoder(jwtDecoder())
    }

    @Bean
    fun jwtDecoder(): JwtDecoder? {
        val secretKey = SecretKeySpec(signingKey.toByteArray(), "HS256")
        logger.info("----動作確認----")
        return NimbusJwtDecoder.withSecretKey(secretKey).build()
    }
}