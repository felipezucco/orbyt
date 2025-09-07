package io.orbyt.config

import io.orbyt.adapter.http.BlackHoleClient
import io.orbyt.adapter.`in`.scheduler.CommunicationRoutine
import io.orbyt.library.port.out.CommunicationGateway
import io.orbyt.domain.model.CommunicationRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OrbytConfig {

    @Bean
    fun blackHoleClient(): CommunicationGateway {
        return BlackHoleClient()
    }

    @Bean
    fun communicationRoutine(registry: CommunicationRegistry,
                             communicationGateway: CommunicationGateway): CommunicationRoutine {
        return CommunicationRoutine(registry, communicationGateway)
    }

}