package io.orbyt.domain.aspect

import io.orbyt.domain.model.registry.BusinessRegistry
import io.orbyt.library.annot.BusinessUnit
import io.orbyt.library.port.out.CommunicationRegistry
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect

@Aspect
class BusinessUnitAspect(
    private val registry: CommunicationRegistry
) {

    @Around("@annotation(businessUnit)")
    fun capture(joinPoint: ProceedingJoinPoint, businessUnit: BusinessUnit): Any? {
        if (registry !is BusinessRegistry)
            return joinPoint.proceed()

        try {
            val result = joinPoint.proceed()
            registry.registerSignal(businessUnit.name)
            return result
        } catch (e: Throwable) {
            registry.registerSignal(businessUnit.name, e.message.toString())
            throw e
        }
    }
}
