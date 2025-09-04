package io.orbyt.domain.model

data class GreetingInfo(
    override val name: String,
    override val apiVersion: String,
    override val frameworkVersion: String,
    val domains: MutableMap<String, *>,
    override val hash: String
): Greeting