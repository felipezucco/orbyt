package io.orbyt.library.clients.okhttp

import io.orbyt.domain.model.CommunicationRegistry
import io.orbyt.domain.model.registry.BusinessRegistry
import okhttp3.Interceptor
import okhttp3.Response

private const val SATELLITE = "SATELLITE"

class OkHttpInterceptor(
    private val communicationRegistry: CommunicationRegistry
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (communicationRegistry !is BusinessRegistry) {
            return chain.proceed(chain.request())
        }

        val request = chain.request()
            .newBuilder()
            .addHeader(SATELLITE, communicationRegistry.key!!)
            .build()

        return chain.proceed(request).also {
            it.headers[SATELLITE]
                .takeIf { it != null && it.isNotEmpty() }
                .let { communicationRegistry.registerSignal(it!!) }
        }
    }
}