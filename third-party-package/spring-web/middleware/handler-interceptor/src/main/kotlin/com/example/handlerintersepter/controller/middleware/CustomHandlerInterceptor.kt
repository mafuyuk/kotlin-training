package com.example.handlerintersepter.controller.middleware

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomHandlerInterceptor : HandlerInterceptor {
    private val logger: Logger = LoggerFactory.getLogger(CustomHandlerInterceptor::class.java)

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        logger.info("preHandle : {}", request)
        Arrays.stream(request.cookies).forEach { cookie ->
            if(cookie.name == "name") {
                logger.info(cookie.value)
            }
        }
        return true
    }
}