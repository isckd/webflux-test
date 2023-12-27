package com.example.webfluxtest.service

import com.example.webfluxtest.controller.TestBody
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class WebClientService(
    private val webClientBuilder: WebClient.Builder
) {

    // webclient 를 사용하여 요청을 보낸다.
    fun hello(): TestBody {
        val webClient = webClientBuilder.build()
        return webClient.post()
            .uri("http://localhost:8080/test/body/test")
            .bodyValue(TestBody("request", 0))
            .retrieve()
            .bodyToMono(TestBody::class.java)
            .expand { testBody ->
                if (testBody.age >= 98) Mono.empty()
                else webClient.post()
                    .uri("http://localhost:8080/test/body/test")
                    .bodyValue(TestBody("request", 0))
                    .retrieve()
                    .bodyToMono(TestBody::class.java)
            }
            .filter { it.age >= 98 }
            .next()
            .block()?: TestBody("testName", 0)

    }
}