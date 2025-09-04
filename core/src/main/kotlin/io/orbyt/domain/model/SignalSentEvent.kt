package io.orbyt.domain.model

import org.springframework.context.ApplicationEvent

class SignalSentEvent(
    source: Any,
    val response: Object? = null,
): ApplicationEvent(source)