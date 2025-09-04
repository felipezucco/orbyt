package io.orbyt.domain.model

import io.orbyt.library.port.out.CommunicationRegistry
import java.util.concurrent.ConcurrentHashMap

class BusinessRegistry: CommunicationRegistry {
    private val data: ConcurrentHashMap<String, Any> = ConcurrentHashMap()
    private val domains: ConcurrentHashMap<String, MutableList<BusinessDomain>> = ConcurrentHashMap()
    private val snapshot: MutableList<BeatingInfo> = mutableListOf()
    private var ready: Boolean = false

    companion object {
        fun instance(): BusinessRegistry = BusinessRegistry()
    }

    private constructor()

    override fun greeting(): GreetingInfo {
        return GreetingInfo(
            name = data["applicationName"] as String,
            apiVersion = data["apiVersion"] as String,
            hash = data["hash"] as String,
            frameworkVersion = data["frameworkVersion"] as String,
            domains = domains
        )
    }

    override fun signal(): List<BeatingInfo> {
        return snapshot
    }

    override fun refresh() {
        snapshot.clear()
    }

    override fun stop() {
        this.ready = false
    }

    fun start() {
        this.ready = true
    }

    override fun ready(): Boolean {
        return this.ready
    }

    fun root(key: String, value: Any) {
        data.put(key, value)
    }

    fun registerDomain(domain: String, info: BusinessUnitInfo) {
        val domains = this.domains.getOrPut(domain, { mutableListOf() })
        domains.find { it.name == info.name }?.also {
            it.businessUnits.add(info)
        }?: domains.add(BusinessDomain(domain, mutableListOf(info)))
    }

}