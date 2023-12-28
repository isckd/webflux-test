package com.example.webfluxtest.webflux

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class WebFluxLoggingFilter : WebFilter {

    private val logger = LoggerFactory.getLogger(WebFluxLoggingFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        logRequest(exchange)
        return chain.filter(exchange)
            .doOnSuccess { logResponse(exchange) }
    }

    private fun logRequest(exchange: ServerWebExchange) {
        val request = exchange.request
        logger.info("Request: {} {}", request.method, request.uri)
    }

    private fun logResponse(exchange: ServerWebExchange) {
        val response = exchange.response
        val status = response.statusCode ?: HttpStatus.OK
        logger.info("Response Status: {}", status)
    }
}