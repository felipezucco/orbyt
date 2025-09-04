package io.orbyt.domain.model

interface Greeting {
    val name: String
    val apiVersion: String
    val frameworkVersion: String
    val hash: String
}