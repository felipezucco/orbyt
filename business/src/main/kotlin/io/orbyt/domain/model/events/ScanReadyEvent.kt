package io.orbyt.domain.model.events

import org.springframework.context.ApplicationEvent

class ScanReadyEvent(source: Any): ApplicationEvent(source)