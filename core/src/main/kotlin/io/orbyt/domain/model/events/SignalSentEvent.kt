package io.orbyt.domain.model.events

import org.springframework.context.ApplicationEvent

class SignalSentEvent(
    source: Any,
    val response: Any? = null,
): ApplicationEvent(source)