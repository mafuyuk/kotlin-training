package com.example.oauth2.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ClientApplication

fun main(args: Array<String>) {
	println("aaa")
	runApplication<ClientApplication>(*args)
}
