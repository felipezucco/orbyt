package io.orbyt.domain.model

import java.time.LocalDateTime

data class BeatingInfo(
    private val _businessUnit: String
): Signal {
    private var _calls: Int = 0
    private var _errors: MutableList<SignalError> = mutableListOf()

    val calls: Int get() = _calls
    val businessUnit: String get() = _businessUnit

    fun calls() {
        this._calls++
    }

    fun calls(error: String?) {
        this.calls()
        error?.let { this._errors.add(SignalError(error, 1, timestamp = LocalDateTime.now().toString())) }
    }

}

data class SignalError(
    val error: String,
    val count: Int,
    val timestamp: String
)