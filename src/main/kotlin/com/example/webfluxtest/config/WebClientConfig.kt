import io.netty.channel.ChannelOption
import io.netty.handler.logging.LogLevel
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.util.concurrent.TimeUnit


@Configuration
class WebClientConfig(
) {

    val log = LoggerFactory.getLogger(WebClientConfig::class.java)

    /**
     *  Connection timeout, Read timeout, Write timeout = 5초 통일
     *  max-in-memory-size : 20MB (default 인 256KB 는 너무 적어 버퍼 사이즈 초과 에러가 발생할 가능성이 높음)
     *  @author ju.ye13
     */

    @Bean
    fun webClientBuilder(): WebClient.Builder {

        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                conn.addHandlerLast(WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
            }
            .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)

        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { configurer: ClientCodecConfigurer ->
                configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)
            }.build()

        return WebClient.builder()
            .exchangeStrategies(exchangeStrategies)
            .clientConnector(ReactorClientHttpConnector(httpClient))
    }

    /**
     * ys 에서 구동되는 undertow WAS 와는 다른 reactor 인 netty 서버와의 메모리를 공유한다.
     * reactor 로 인해 메모리 누수가 생기는 것을 방지하기 위함
     */

    @Bean
    fun reactorResourceFactory(): ReactorResourceFactory {
        val factory = ReactorResourceFactory().apply {
            isUseGlobalResources = true
        }
        return factory
    }


}
