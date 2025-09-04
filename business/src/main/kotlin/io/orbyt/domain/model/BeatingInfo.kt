package io.orbyt.domain.model

import io.orbyt.library.port.out.BeatingErrorInterface
import java.time.LocalDateTime

data class BeatingInfo(
    private val _businessUnit: String
): Signal {
    private var _calls: Int = 0
    private var _errors: MutableList<BeatingErrorInterface> = mutableListOf()

    val calls: Int get() = _calls
    val businessUnit: String get() = _businessUnit

    fun calls() {
        this._calls++
    }

    fun calls(error: String?) {
        this.calls()
        error?.let { this._errors.add(BeatingError(error, 1, timestamp = LocalDateTime.now().toString())) }
    }

}

data class BeatingError(
    override val error: String,
    override val count: Int,
    override val timestamp: String
): BeatingErrorInterface