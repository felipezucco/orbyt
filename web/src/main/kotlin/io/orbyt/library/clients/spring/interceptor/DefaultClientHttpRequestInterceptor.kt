package io.orbyt.library.clients.spring.interceptor

import io.orbyt.domain.model.CommunicationRegistry
import io.orbyt.domain.model.registry.BusinessRegistry
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

private const val SATELLITE = "SATELLITE"

open class DefaultClientHttpRequestInterceptor(
    private val communicationRegistry: CommunicationRegistry
): ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        if (communicationRegistry !is BusinessRegistry) {
            return execution.execute(request, body)
        }

        request.headers.add(SATELLITE, communicationRegistry.key!!)

        return execution.execute(request, body).also {
            it.headers.get(SATELLITE)
                .takeIf { it != null && it.isNotEmpty() }
                ?.firstNotNullOf { communicationRegistry.registerSignal(it!!) }
        }
    }
}