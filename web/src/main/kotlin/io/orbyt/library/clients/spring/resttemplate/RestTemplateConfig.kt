package io.orbyt.library.clients.spring.resttemplate

import io.orbyt.domain.model.CommunicationRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

@Configuration
@ConditionalOnClass(RestTemplate::class)
class RestTemplateConfig {

    @Bean
    fun restTemplateCustom(interceptor: RestTemplateHttpRequestInterceptor): RestTemplateBuilder {
        return RestTemplateBuilder().additionalInterceptors(listOf(interceptor))
    }

    @Bean
    fun restTemplateInterceptor(communicationRegistry: CommunicationRegistry): RestTemplateHttpRequestInterceptor {
        return RestTemplateHttpRequestInterceptor(communicationRegistry)
    }

}