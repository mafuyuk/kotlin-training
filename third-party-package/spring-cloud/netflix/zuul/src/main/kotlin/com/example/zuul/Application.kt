package com.example.zuul

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@EnableZuulProxy
@SpringBootApplication
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}

@Bean
fun simpleFilter(): SimpleFilter {
	return SimpleFilter()
}