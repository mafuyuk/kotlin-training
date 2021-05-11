package com.example.cookie.controller

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @GetMapping("home")
    fun getHome(): ResponseEntity<String> {
        return ResponseEntity.ok("home")
    }
}