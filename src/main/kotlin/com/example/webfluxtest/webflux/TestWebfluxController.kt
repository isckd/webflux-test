package com.example.webfluxtest.webflux

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/webflux")
class TestWebfluxController {

    @GetMapping("/test")
    fun test(): Mono<String> {
        return Mono.just("webflux test")
    }
}