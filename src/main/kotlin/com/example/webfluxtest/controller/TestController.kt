package com.example.webfluxtest.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/test")
class TestController(
    private val restTemplate: RestTemplate
) {

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello, World!"
    }

    @GetMapping("/world")
    fun world(): String {
        restTemplate.getForObject("http://localhost:8080/test/hello", String::class.java)
        return "Hello, World!"
    }
}