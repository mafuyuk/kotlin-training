package com.example.handlerintersepter.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    private val logger: Logger = LoggerFactory.getLogger(HomeController::class.java)

    @GetMapping("home")
    fun getHome(): ResponseEntity<String> {
        logger.info("curl home")
        return ResponseEntity.ok("home")
    }
}