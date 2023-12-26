package com.example.webfluxtest.controller

import com.example.webfluxtest.service.WebfluxService
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import kotlin.random.Random

@RestController
@RequestMapping("/test")
class TestController(
    private val restTemplate: RestTemplate,
    private val webfluxService: WebfluxService
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


    @PostMapping("/body/test")
    fun bodyTest(@RequestBody testBody: TestBody): TestBody {
        return TestBody("testName", Random.nextInt(100) + 1)
    }

    @GetMapping("/test1")
    fun test1(): TestBody {
        val testBody = webfluxService.hello()
        return testBody
    }
}

data class TestBody(
    val name: String,
    val age: Int
)