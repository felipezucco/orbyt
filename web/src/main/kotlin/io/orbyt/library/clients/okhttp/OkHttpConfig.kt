package io.orbyt.library.clients.okhttp

import io.orbyt.domain.model.CommunicationRegistry
import okhttp3.OkHttpClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(OkHttpClient::class)
class OkHttpConfig {

    @Bean
    fun okHttpCustom(interceptor: OkHttpInterceptor): OkHttpClient.Builder {
        return OkHttpClient.Builder().also { it.addInterceptor(interceptor) }
    }

    @Bean
    fun okHttpInterceptor(communicationRegistry: CommunicationRegistry): OkHttpInterceptor {
        return OkHttpInterceptor(communicationRegistry)
    }

}