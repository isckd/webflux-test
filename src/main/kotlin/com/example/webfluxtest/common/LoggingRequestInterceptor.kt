package com.example.webfluxtest.common

import org.slf4j.MDC
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class LoggingRequestInterceptor(
    private val applicationName: String
): ClientHttpRequestInterceptor {
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

