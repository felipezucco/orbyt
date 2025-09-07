package io.orbyt.library.config

import io.orbyt.domain.model.registry.BusinessRegistry
import io.orbyt.domain.model.CommunicationRegistry
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

private const val SATELLITE = "SATELLITE"

class InterceptHeader(
    val communicationRegistry: CommunicationRegistry
): HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (this.communicationRegistry is BusinessRegistry) {
            request.getHeader(SATELLITE)
                .takeIf { it != null }
                ?.let { communicationRegistry.registerSignal(it) }

            response.addHeader(SATELLITE, communicationRegistry.key)
        }

        return super.preHandle(request, response, handler)
    }

}