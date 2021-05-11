package com.example.webflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters


@SpringBootApplication
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}

@RestController
@RequestMapping
class FooController() {

	@GetMapping("/foo")
	fun foo() = ResponseEntity
		.ok()
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromValue("foo"))
}
