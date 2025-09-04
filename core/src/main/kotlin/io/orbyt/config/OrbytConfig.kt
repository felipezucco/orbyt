package io.orbyt.config

import io.orbyt.adapter.http.BlackHoleClient
import io.orbyt.adapter.`in`.scheduler.CommunicationRoutine
import io.orbyt.library.port.out.CommunicationGateway
import io.orbyt.library.port.out.CommunicationRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class OrbytConfig {

    @Bean
    fun blackHoleClient(): CommunicationGateway {
        return BlackHoleClient()
    }

    @Bean
    fun communicationScheduler(registry: CommunicationRegistry,
                               communicationGateway: CommunicationGateway): CommunicationRoutine {
        return CommunicationRoutine(registry, communicationGateway)
    }

}