package io.orbyt.library.port.out

import io.orbyt.domain.model.Greeting
import io.orbyt.domain.model.Signal
import okhttp3.Response

interface CommunicationGateway {

    suspend fun pointAntenna(pInfo: Greeting): Response

    suspend fun transmitSignal(pInfo: List<Signal>): Response

}