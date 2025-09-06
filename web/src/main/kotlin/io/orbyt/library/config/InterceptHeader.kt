package io.orbyt.library.config

import io.orbyt.domain.model.registry.BusinessRegistry
import io.orbyt.library.port.out.CommunicationRegistry
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.web.servlet.HandlerInterceptor

class InterceptHeader(
    val communicationRegistry: CommunicationRegistry
): HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (this.communicationRegistry is BusinessRegistry) {
            request.getHeader("X-Request-Id")
                .takeIf { it != null }
                ?.let { communicationRegistry.registerSignal(it) }
        }

        return super.preHandle(request, response, handler)
    }

}