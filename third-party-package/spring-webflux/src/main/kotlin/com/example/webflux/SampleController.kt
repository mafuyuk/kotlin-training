package com.example.webflux

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class SampleController {
    @GetMapping("/sample")
    fun sample(): Mono<String> {
        return Mono.just("Hello World")
    }
}