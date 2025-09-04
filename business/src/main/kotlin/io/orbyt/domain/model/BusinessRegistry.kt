package io.orbyt.domain.model

import io.orbyt.library.port.out.CommunicationRegistry
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.Delegates

class BusinessRegistry: CommunicationRegistry {
    private val _data: ConcurrentHashMap<String, Any> = ConcurrentHashMap()
    private val _domains: ConcurrentHashMap<String, MutableList<BusinessDomain>> = ConcurrentHashMap()
    private val _snapshot: MutableList<BeatingInfo> = mutableListOf()
    private var _ready: Boolean = false

    companion object {
        fun instance(): BusinessRegistry = BusinessRegistry()
    }

    private constructor()

    override fun greeting(): GreetingInfo {
        return GreetingInfo(
            name = _data["applicationName"] as String,
            apiVersion = _data["apiVersion"] as String,
            hash = _data["hash"] as String,
            frameworkVersion = _data["frameworkVersion"] as String,
            domains = _domains
        )
    }

    override fun signal(): List<BeatingInfo> {
        return this._snapshot
    }

    override fun refresh() {
        this._snapshot.clear()
        this.stop()
    }

    override fun stop() {
        this._ready = false
    }

    fun start() {
        this._ready = true
    }

    override fun ready(): Boolean {
        return this._ready
    }

    fun root(key: String, value: Any) {
        _data.put(key, value)
    }

    fun registerDomain(domain: String, info: BusinessUnitInfo) {
        val domains = this._domains.getOrPut(domain, { mutableListOf() })
        domains.find { it.name == info.name }?.also {
            it.businessUnits.add(info)
        }?: domains.add(BusinessDomain(domain, mutableListOf(info)))
    }

    fun registerSignal(businessUnit: String, error: String? = null) {
        _snapshot.find { it.businessUnit == businessUnit }?.calls(error) ?:
        _snapshot.add(BeatingInfo(businessUnit).apply { calls(error) })

        if (!this._ready) {
            this.start()
        }
    }

}