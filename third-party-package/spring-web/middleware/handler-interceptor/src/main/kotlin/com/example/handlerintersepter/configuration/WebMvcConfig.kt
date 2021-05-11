package com.example.handlerintersepter.configuration

import com.example.handlerintersepter.controller.middleware.CustomHandlerInterceptor
import org.springframework.context.annotation.Configuration

import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebMvcConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(CustomHandlerInterceptor())
            .addPathPatterns("/home")
            .excludePathPatterns("/static/**") // 除外するパス(パターン)を指定する
    }
}