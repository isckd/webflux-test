package com.example.webfluxtest.common

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class LoggingFilter : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss")
        val formattedDate = sdf.format(System.currentTimeMillis())
        if (request.getHeader("traceId") != null) {
            MDC.put("traceId", request.getHeader("traceId"))
        } else {
            MDC.put("traceId", formattedDate + "-" + UUID.randomUUID().toString().substring(0, 8))
        }
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response)
        } else {
            doFilterWrapped(RequestWrapper(request), ResponseWrapper(response), filterChain)
        }
        MDC.clear()
    }

    @Throws(ServletException::class, IOException::class)
    protected fun doFilterWrapped(
        request: RequestWrapper,
        response: ContentCachingResponseWrapper,
        filterChain: FilterChain
    ) {
        var fullURI = ""
        val startTime = System.currentTimeMillis()
        try {
            fullURI = logRequest(request)
            filterChain.doFilter(request, response)
        } finally {
            val duration = System.currentTimeMillis() - startTime
            logResponse(getCaller(request.getHeader("caller")?: ""), request.method, fullURI, response, duration)
            response.copyBodyToResponse()
        }
    }

    companion object {
        protected val log = LoggerFactory.getLogger(LoggingFilter::class.java)
        private fun modifyQueryString(queryString: String?): String {
            return if (queryString == null || queryString.isEmpty()) {
                ""
            } else Arrays.stream(queryString.split("&".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
                .filter { param: String -> !param.startsWith("uri=") }
                .collect(Collectors.joining("&"))
        }

        @Throws(IOException::class)
        private fun logRequest(request: RequestWrapper): String {
            val uri: String = request.requestURI
            val queryString: String = request.queryString?: ""

            // 수정된 쿼리 스트링을 생성합니다.
            val modifiedQueryString = modifyQueryString(queryString)

            // 수정된 전체 URI를 조합합니다.
            val fullURI = uri + if (modifiedQueryString.isEmpty()) "" else "?$modifiedQueryString"
            val payload = logPayload("", request.contentType, request.inputStream)
            val caller: String? = request.getHeader("caller")
            log.info(
                "[{}-REQ] {} {} {}",
                getCaller(caller?: ""),
                request.method,
                fullURI,
                payload ?: ""
            )
            return fullURI
        }

        @Throws(IOException::class)
        private fun logResponse(
            caller: String,
            method: String,
            fullURI: String,
            response: ContentCachingResponseWrapper,
            duration: Long
        ) {
            val payload = logPayload("", response.contentType, response.contentInputStream)
            log.info(
                "[{}-RES] {} {} {} {}ms",
                caller,
                method,
                fullURI,
                payload ?: "",
                duration
            )
            logPayload("[RES]", response.contentType, response.contentInputStream)
        }

        @Throws(IOException::class)
        private fun logPayload(prefix: String, contentType: String?, inputStream: InputStream): String? {
            val visible = isVisible(MediaType.valueOf(contentType ?: "application/json"))
            if (visible) {
                val content = StreamUtils.copyToByteArray(inputStream)
                if (content.size > 0) {
                    val contentString = String(content).replace("\\s+".toRegex(), " ")
                    return prefix + contentString
                }
            } else {
                return "$prefix Payload: Binary Content"
            }
            return null
        }

        private fun getCaller(caller: String): String {
            return if (caller.isNotEmpty()) {
                caller.substring(1).uppercase(Locale.getDefault())
            } else {
                "CLIENT"
            }
            return caller
        }

        private fun isVisible(mediaType: MediaType): Boolean {
            val VISIBLE_TYPES = Arrays.asList(
                MediaType.valueOf("text/*"),
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.valueOf("application/*+json"),
                MediaType.valueOf("application/*+xml"),
                MediaType.MULTIPART_FORM_DATA
            )
            return VISIBLE_TYPES.stream()
                .anyMatch { visibleType: MediaType ->
                    visibleType.includes(
                        mediaType
                    )
                }
        }
    }

}


