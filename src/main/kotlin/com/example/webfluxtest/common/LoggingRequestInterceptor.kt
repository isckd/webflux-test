package com.example.webfluxtest.common

import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


@Configuration
class LoggingRequestInterceptor(
): ClientHttpRequestInterceptor {

    @Value("\${spring.application.name}")
    private val applicationName: String? = null

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {

        MDC.get("traceId")?.let { request.headers.add("traceId", it) }

        (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.let {
            request.headers.add("caller", applicationName)
        }

        return execution.execute(request, body)
    }
}

