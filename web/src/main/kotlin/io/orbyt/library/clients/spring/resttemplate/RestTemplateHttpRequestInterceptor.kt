package io.orbyt.library.clients.spring.resttemplate

import io.orbyt.domain.model.CommunicationRegistry
import io.orbyt.library.clients.spring.interceptor.DefaultClientHttpRequestInterceptor

class RestTemplateHttpRequestInterceptor(
    communicationRegistry: CommunicationRegistry
): DefaultClientHttpRequestInterceptor(communicationRegistry)