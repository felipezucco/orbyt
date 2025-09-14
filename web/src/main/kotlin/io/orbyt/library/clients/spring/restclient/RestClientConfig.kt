package io.orbyt.library.clients.spring.restclient

import io.orbyt.domain.model.CommunicationRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
@ConditionalOnClass(RestClient::class)
class RestClientConfig {

    @Bean
    fun restClientCustom(interceptor: RestClientHttpRequestInterceptor): RestClient.Builder {
        return RestClient.builder().also { it.requestInterceptor(interceptor) }
    }

    @Bean
    fun restClientInterceptor(communicationRegistry: CommunicationRegistry): RestClientHttpRequestInterceptor {
        return RestClientHttpRequestInterceptor(communicationRegistry)
    }

}