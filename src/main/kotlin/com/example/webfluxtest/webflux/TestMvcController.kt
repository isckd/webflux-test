package com.example.webfluxtest.webflux

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mvc")
class TestMvcController {

    @GetMapping("/test")
    fun test(): String {
        return "mvc test"
    }
}