package io.orbyt.domain.model.events

import org.springframework.context.ApplicationEvent

class CommunicationReadyEvent(source: Any): ApplicationEvent(source)