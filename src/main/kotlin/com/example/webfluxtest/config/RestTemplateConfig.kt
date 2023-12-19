package com.example.webfluxtest.config

import com.example.webfluxtest.common.LoggingRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {


    @Configuration
    class Config {
        @Bean
        fun restTemplate(): RestTemplate {
            val restTemplate = RestTemplate()
            restTemplate.interceptors = listOf<ClientHttpRequestInterceptor>(LoggingRequestInterceptor())
            return restTemplate
        }
    }

}