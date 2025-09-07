package io.orbyt.domain.model

interface CommunicationRegistry {
    var key: String?

    fun greeting(): Greeting

    fun signal(): List<Signal>

    fun refresh()

    fun stop()

    fun ready(): Boolean

}