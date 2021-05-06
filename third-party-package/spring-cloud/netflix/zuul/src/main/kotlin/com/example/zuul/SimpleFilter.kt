package com.example.zuul

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest


class SimpleFilter : ZuulFilter() {
    private val log = LoggerFactory.getLogger(SimpleFilter::class.java)

    override fun filterType(): String {
        return "pre"
    }

    override fun filterOrder(): Int {
        return 1
    }

    override fun shouldFilter(): Boolean {
        return true
    }

    override fun run(): Any? {
        val ctx: RequestContext = RequestContext.getCurrentContext()
        val request: HttpServletRequest = ctx.getRequest()
        log.info(java.lang.String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()))
        return null
    }
}