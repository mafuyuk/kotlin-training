package com.example.zuul

import com.netflix.zuul.ZuulFilter
import com.sun.xml.internal.ws.client.RequestContext
import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory


class SimpleFilter : ZuulFilter() {
    private val log = LoggerFactory.getLogger(SimpleFilter::class.java)

    fun filterType(): String {
        return "pre"
    }

    fun filterOrder(): Int {
        return 1
    }

    fun shouldFilter(): Boolean {
        return true
    }

    fun run(): Any? {
        val ctx: RequestContext = RequestContext.getCurrentContext()
        val request: HttpServletRequest = ctx.getRequest()
        log.info(java.lang.String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()))
        return null
    }
}