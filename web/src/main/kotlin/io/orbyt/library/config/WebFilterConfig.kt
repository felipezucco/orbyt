package io.orbyt.library.config

import io.orbyt.library.port.out.CommunicationRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebFilterConfig: WebMvcConfigurer {

    @Bean
    fun interceptHeader(communicationRegistry: CommunicationRegistry): InterceptHeader {
        return InterceptHeader(communicationRegistry)
    }

    @Bean
    fun customWebMvcConfigurer(interceptHeader: InterceptHeader): WebMvcConfigurer {
        return CustomWebMvcConfigurer(interceptHeader)
    }

}