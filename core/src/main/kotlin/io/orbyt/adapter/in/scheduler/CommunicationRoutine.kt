package io.orbyt.adapter.`in`.scheduler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.orbyt.domain.model.events.CommunicationReadyEvent
import io.orbyt.domain.model.events.SignalSentEvent
import io.orbyt.library.port.out.CommunicationGateway
import io.orbyt.domain.model.CommunicationRegistry
import io.orbyt.domain.model.GreetingResponse
import io.orbyt.domain.model.events.GreetingSentEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okio.use
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.context.ApplicationListener
import org.springframework.context.event.EventListener
import kotlin.time.Duration.Companion.seconds

class CommunicationRoutine(
    private val registry: CommunicationRegistry,
    private val communicationGateway: CommunicationGateway
): ApplicationEventPublisherAware, AutoCloseable {

    private val handler = CoroutineExceptionHandler { asd, ex ->
        println("Erro na CommunicationRoutine: ${ex.message}")
    }

    private val _scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + handler)
    private var _applicationEventPublisher: ApplicationEventPublisher? = null
    private var beatJob: Job? = null

    @EventListener(CommunicationReadyEvent::class)
    fun onCommunicationReady(event: CommunicationReadyEvent) {
        _scope.launch {
            val response = communicationGateway.pointAntenna(registry.greeting())
            val greetingResponse = jacksonObjectMapper()
                .readValue(response.body?.string(), GreetingResponse::class.java)
            println("Thread [${Thread.currentThread().name}] - Antenna response: ${response.code}")

            _applicationEventPublisher?.publishEvent(GreetingSentEvent(this, greetingResponse))
        }
    }

    @EventListener(GreetingSentEvent::class)
    fun onGreetingSent(event: GreetingSentEvent) {
        beatJob?.cancel()
        beatJob = _scope.launch {
            while (isActive) {
                if (registry.ready()) {
                    beat()
                }
                delay(5.seconds)
            }
        }
    }

    suspend fun beat() {
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