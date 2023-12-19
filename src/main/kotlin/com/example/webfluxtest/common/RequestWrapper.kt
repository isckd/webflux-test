package com.example.webfluxtest.common

import org.springframework.util.StreamUtils
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class RequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private val cachedInputStream: ByteArray = StreamUtils.copyToByteArray(request.inputStream)

    override fun getInputStream(): ServletInputStream {
        return object : ServletInputStream() {
            private val cachedBodyInputStream = ByteArrayInputStream(cachedInputStream)

            override fun isFinished(): Boolean {
                return try {
                    cachedBodyInputStream.available() == 0
                } catch (e: IOException) {
                    e.printStackTrace()
                    false
                }
            }

            override fun isReady(): Boolean {
                return true
            }

            override fun setReadListener(readListener: ReadListener?) {
                throw UnsupportedOperationException()
            }

            @Throws(IOException::class)
            override fun read(): Int {
                return cachedBodyInputStream.read()
            }
        }
    }
}
