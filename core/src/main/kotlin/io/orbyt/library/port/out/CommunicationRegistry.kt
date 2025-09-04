package io.orbyt.library.port.out

import io.orbyt.domain.model.Greeting
import io.orbyt.domain.model.Signal

interface CommunicationRegistry {

    fun greeting(): Greeting

    fun signal(): List<Signal>

    fun refresh()

    fun stop()

    fun ready(): Boolean

}