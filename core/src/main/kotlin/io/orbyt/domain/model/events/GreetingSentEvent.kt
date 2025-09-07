package io.orbyt.domain.model.events

import io.orbyt.domain.model.GreetingResponse
import org.springframework.context.ApplicationEvent

class GreetingSentEvent(
    source: Any,
    val greetingResponse: GreetingResponse
): ApplicationEvent(source)