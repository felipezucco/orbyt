package io.orbyt.domain.aspect

import io.orbyt.library.annot.BusinessUnit
import io.orbyt.library.port.out.CommunicationRegistry
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class BusinessUnitAspect(
    private val registry: CommunicationRegistry
) {

    @Around("@annotation(businessUnit)")
    fun capture(joinPoint: ProceedingJoinPoint, businessUnit: BusinessUnit): Any? {
        try {
            return joinPoint.proceed()
        } catch (ex: Exception) {
//            CommunicationRegistry.register(BusinessUnit.name, ex.message.toString())
            throw ex
        } finally {
//            CommunicationRegistry.register(BusinessUnit.name)
        }
    }
}
