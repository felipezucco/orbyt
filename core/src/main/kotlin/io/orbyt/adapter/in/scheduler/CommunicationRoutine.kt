package io.orbyt.adapter.`in`.scheduler

import io.orbyt.domain.model.SignalSentEvent
import io.orbyt.library.port.out.CommunicationGateway
import io.orbyt.library.port.out.CommunicationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import java.util.Objects
import kotlin.time.Duration.Companion.seconds

class CommunicationRoutine(
    private val registry: CommunicationRegistry,
    private val communicationGateway: CommunicationGateway
): ApplicationEventPublisherAware {

    private val _scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var _applicationEventPublisher: ApplicationEventPublisher? = null

    init {
        _scope.launch {
            while (!registry.ready()) {
                println("Thread [${Thread.currentThread().name}] - Registry not ready yet, waiting...")
                delay(5.seconds)
            }

            val response = communicationGateway.pointAntenna(registry.greeting())
            println("Thread [${Thread.currentThread().name}] - Antenna response: ${response.code}")

            launch {
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

}