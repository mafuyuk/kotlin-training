package com.example.zuul

import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableZuulProxy
@Configuration
class ZuulConfiguration

@Bean
fun simpleFilter(): SimpleFilter {
    return SimpleFilter()
}