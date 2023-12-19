package com.example.webfluxtest.config

import com.example.webfluxtest.common.LoggingRequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @Bean
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.interceptors = listOf<ClientHttpRequestInterceptor>(LoggingRequestInterceptor(applicationName))
        return restTemplate
    }


}