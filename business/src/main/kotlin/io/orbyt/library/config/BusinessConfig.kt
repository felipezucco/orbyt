package io.orbyt.library.config

import io.orbyt.domain.aspect.BusinessUnitAspect
import io.orbyt.domain.model.BusinessUnitScanner
import io.orbyt.domain.model.registry.BusinessRegistry
import io.orbyt.domain.model.CommunicationRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.SpringVersion

@Configuration
class BusinessConfig(
    @Value("\${spring.application.name}")
    private val appName: String
) {

    @Bean
    fun businessRegistry(): CommunicationRegistry {
        val registry = BusinessRegistry.instance()
        registry.root("applicationName", appName)
        registry.root("apiVersion", "1.0.0")
        registry.root("hash", "gasdgasdg")
        SpringVersion.getVersion()?.let { registry.root("frameworkVersion", it) }
        return registry
    }

    @Bean
    fun businessUnitScanner(registry: CommunicationRegistry): BusinessUnitScanner {
        return BusinessUnitScanner(registry)
    }

    @Bean
    fun businessUnitAspect(registry: CommunicationRegistry): BusinessUnitAspect {
        return BusinessUnitAspect(registry)
    }

}