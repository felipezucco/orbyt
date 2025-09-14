package io.orbyt.library.clients.spring.restclient

import io.orbyt.domain.model.CommunicationRegistry
import io.orbyt.library.clients.spring.interceptor.DefaultClientHttpRequestInterceptor

class RestClientHttpRequestInterceptor(
    communicationRegistry: CommunicationRegistry
): DefaultClientHttpRequestInterceptor(communicationRegistry)