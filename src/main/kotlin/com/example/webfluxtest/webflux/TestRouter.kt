package com.example.webfluxtest.webflux

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

/**
 * Spring Webflux 는 두 가지 형식의 api endPoint 를 제공하는데,
 * 1. annotation controller (기존 mvc 방식)   2. functional endpoint
 * 아래 방식은 2번 형식이지만, 문제는 아래 방식을 사용하려면 build.gradle 에 spring-boot-starter-web 부분을 제거해야 적용이 가능하다.
 * spring-boot-starter-web 는 상위 모듈에서 하위 모듈에 전체 뿌리는 구조이기도 하고, swagger 와 openApi 를 사용하려면
 * functional endPoint 방식은 아직 호환성이 너무나도 부족하다.
 */

//@Configuration
//class TestRouter {
//
//    @Bean
//    fun coRoutes(handler: TestHandler) = coRouter {
//        "/webflux".nest {
//            GET("/test", handler::test)
//        }
//    }
//}