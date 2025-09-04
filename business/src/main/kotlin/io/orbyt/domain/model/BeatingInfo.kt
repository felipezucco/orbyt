package io.orbyt.domain.model

import io.orbyt.library.port.out.BeatingErrorInterface
import java.time.LocalDateTime

data class BeatingInfo(
    val businessUnit: String
): Signal {
    var calls: Int = 0
    var errors: MutableList<BeatingErrorInterface> = mutableListOf()

    fun addError(error: String) {
        this.errors.add(BeatingError(error, 1, timestamp = LocalDateTime.now().toString()))
    }

}

data class BeatingError(
    override val error: String,
    override val count: Int,
    override val timestamp: String
): BeatingErrorInterface