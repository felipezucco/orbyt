package io.orbyt.library.config

import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

class CustomWebMvcConfigurer(
    val interceptHeader: InterceptHeader
): WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(this.interceptHeader)
        super.addInterceptors(registry)
    }
    
}