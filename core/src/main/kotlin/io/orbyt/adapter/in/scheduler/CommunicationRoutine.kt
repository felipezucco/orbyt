package io.orbyt.adapter.`in`.scheduler

import io.orbyt.library.port.out.CommunicationGateway
import io.orbyt.library.port.out.CommunicationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class CommunicationRoutine(
    private val registry: CommunicationRegistry,
    private val communicationGateway: CommunicationGateway
) {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch {
            // espera até que o registry esteja pronto
            while (!registry.ready()) {
                println("Thread [${Thread.currentThread().name}] - Registry not ready yet, waiting...")
                delay(5.seconds)
            }

            // inicializa a antena
            val response = communicationGateway.pointAntenna(registry.greeting())
            println("Thread [${Thread.currentThread().name}] - Antenna response: ${response.code}")

            // inicia o "heartbeat" a cada 5 segundos
            launch {
                while (isActive) {
                    val beatResponse = communicationGateway.transmitSignal(registry.signal())
                    println("Thread [${Thread.currentThread().name}] - Heartbeat response: ${beatResponse.code}")
                    delay(5.seconds)
                }
            }
        }
    }

    fun shutdown() {
        scope.cancel() // cancela todas as coroutines associadas
    }

    fun beat(): Runnable = Runnable {
        val response = communicationGateway.transmitSignal(registry.signal())
        println("Thread [${Thread.currentThread().name}] - Heartbeat response: ${response.code}")
    }

}