package com.example

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class Controller {
    @Value("\${app.hello}")
    val hello = ""

    @RequestMapping("/hello", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun hello(): ResponseEntity<String> {
        return ResponseEntity<String>(
            hello,
            HttpStatus.OK
        )
    }

    @RequestMapping("/error")
    fun error(): ResponseEntity<String> {
        if (true) {
            throw Exception("error")
        }
        return ResponseEntity<String>(
            "error",
            HttpStatus.OK
        )
    }
}
