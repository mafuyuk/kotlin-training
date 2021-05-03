package com.oauth2.clientcredentials.resourceserver.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class UserController {

    val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("user")
    fun getUser(): ResponseEntity<String> {
        return ResponseEntity.ok("users")
    }

    @GetMapping("user/{id}")
    fun getUserById(
        @AuthenticationPrincipal principal: Jwt,
        @PathVariable("id") id: Int?
    ): ResponseEntity<Int> {
        logger.info(principal.getClaimAsString("scope")) // claimの利用
        return ResponseEntity.ok(id)
    }
}