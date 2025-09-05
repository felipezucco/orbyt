package io.orbyt.adapter.`in`.scheduler

import io.orbyt.domain.model.events.CommunicationReadyEvent
import io.orbyt.domain.model.events.SignalSentEvent
import io.orbyt.library.port.out.CommunicationGateway
import io.orbyt.library.port.out.CommunicationRegistry
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.context.ApplicationListener
import kotlin.time.Duration.Companion.seconds

class CommunicationRoutine(
    private val registry: CommunicationRegistry,
    private val communicationGateway: CommunicationGateway
): ApplicationEventPublisherAware, ApplicationListener<CommunicationReadyEvent>, AutoCloseable {

    private val handler = CoroutineExceptionHandler { _, ex ->
        println("Erro na CommunicationRoutine: ${ex.message}")
    }

    private val _scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + handler)
    private var _applicationEventPublisher: ApplicationEventPublisher? = null
    private var beatJob: Job? = null

    override fun onApplicationEvent(event: CommunicationReadyEvent) {
        _scope.launch {
            val response = communicationGateway.pointAntenna(registry.greeting())
            println("Thread [${Thread.currentThread().name}] - Antenna response: ${response.code}")

            beatJob = _scope.launch {
                while (isActive) {
                    if (registry.ready()) {
                        beat()
                    }
                    delay(5.seconds)
                }
            }
        }
    }

    fun beat() {
        val response = communicationGateway.transmitSignal(registry.signal())
            .also {
                it.takeIf { it.isSuccessful }.run { registry.refresh() }
                _applicationEventPublisher?.publishEvent(SignalSentEvent(this))
            }
        println("Thread [${Thread.currentThread().name}] - Heartbeat response: ${response.code}")
    }

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        this._applicationEventPublisher = applicationEventPublisher
    }

    override fun close() {
        beatJob?.cancel()
        _scope.cancel()
    }
}