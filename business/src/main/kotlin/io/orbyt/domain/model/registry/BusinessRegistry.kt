package io.orbyt.domain.model.registry

import io.orbyt.domain.model.BeatingInfo
import io.orbyt.domain.model.BusinessDomain
import io.orbyt.domain.model.BusinessUnitInfo
import io.orbyt.domain.model.GreetingInfo
import io.orbyt.domain.model.events.CommunicationReadyEvent
import io.orbyt.domain.model.events.ScanReadyEvent
import io.orbyt.domain.model.CommunicationRegistry
import io.orbyt.domain.model.events.GreetingSentEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.context.ApplicationListener
import org.springframework.context.event.EventListener
import java.util.concurrent.ConcurrentHashMap

class BusinessRegistry: CommunicationRegistry, ApplicationEventPublisherAware {
    private val _data: ConcurrentHashMap<String, Any> = ConcurrentHashMap()
    private val _domains: ConcurrentHashMap<String, MutableList<BusinessDomain>> = ConcurrentHashMap()
    private val _snapshot: MutableList<BeatingInfo> = mutableListOf()
    private var _ready: Boolean = false
    private var _applicationEventPublisher: ApplicationEventPublisher? = null

    private var _key: String? = null

    companion object {
        fun instance(): BusinessRegistry = BusinessRegistry()
    }

    private constructor()

    override var key: String?
        get() = this._key
        set(value) {}

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
        this._data.put(key, value)
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

    @EventListener(GreetingSentEvent::class)
    fun onGreetingSent(event: GreetingSentEvent) {
        this._key = event.greetingResponse.key
    }

    @EventListener(ScanReadyEvent::class)
    fun onScanReady(event: ScanReadyEvent) {
        this.start()
        this._applicationEventPublisher?.publishEvent(CommunicationReadyEvent(this))
    }

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        this._applicationEventPublisher = applicationEventPublisher
    }
}