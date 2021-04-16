package com.example.oauth2.clientcredentials.controller

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class UserController {
    @GetMapping("user/{id}")
    fun getUserById(@PathVariable("id") id: Int?): ResponseEntity<Int> {
        return ResponseEntity.ok(id)
    }
}