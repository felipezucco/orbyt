package io.orbyt.domain.model

import io.orbyt.domain.annot.EnableOrbit
import io.orbyt.domain.model.events.ScanReadyEvent
import io.orbyt.domain.model.registry.BusinessRegistry
import io.orbyt.library.annot.BusinessUnit
import io.orbyt.library.port.out.CommunicationRegistry
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.type.filter.AssignableTypeFilter
import java.security.MessageDigest
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

class BusinessUnitScanner(
    private val registry: CommunicationRegistry
): ApplicationEventPublisherAware, ApplicationListener<ContextRefreshedEvent> {

    private val handler = CoroutineExceptionHandler { _, ex ->
        println("Erro no BusinessUnitScanner: ${ex.message}")
    }

    private val _scope : CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)
    private var _eventPublisher : ApplicationEventPublisher? = null

    private fun generateClassHash(clazz: Class<*>): String {
        val bytes = clazz.getResourceAsStream("/${clazz.name.replace('.', '/')}.class")?.readBytes()
            ?: return ""
        return bytes.sha256()
    }

    private fun ByteArray.sha256(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(this)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun onCreate(context: ApplicationContext) {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AssignableTypeFilter(Any::class.java))

        findRootPackages(context).let { pkg -> pkg.forEach { this.scan(scanner, it) }}
    }

    private fun scan(scanner: ClassPathScanningCandidateComponentProvider, rootPackage: String) {
        val candidates = scanner.findCandidateComponents(rootPackage)
        candidates.forEach { beanDef ->
            val clazz = Class.forName(beanDef.beanClassName)

            clazz.kotlin.declaredFunctions.forEach { function ->
                val annotation = function.findAnnotation<BusinessUnit>()
                if (annotation != null) {
                    (registry as BusinessRegistry).registerDomain(
                        annotation.domain,
                        BusinessUnitInfo(
                            name = annotation.name,
                            method = "${function.name}()",
                            clazz = clazz.simpleName.toString(),
                            hash = generateClassHash(clazz)
                        )
                    )
                }
            }
        }
    }

    private fun findRootPackages(context: ApplicationContext): Array<String> {
        val mainBeanName = context.getBeanNamesForAnnotation(EnableOrbit::class.java)
            .firstOrNull()
            ?: throw IllegalArgumentException("EnableOrbit annotation not found")

        val mainClass = context.getType(mainBeanName)
            ?: throw IllegalArgumentException("Cannot determine class for bean $mainBeanName")

        // busca recursiva, incluindo meta-anotações
        val annotation = AnnotatedElementUtils.findMergedAnnotation(mainClass, EnableOrbit::class.java)
            ?: throw IllegalArgumentException("@EnableOrbyt annotation not present on class $mainClass")

        return annotation.basePackages
    }

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        this._eventPublisher = applicationEventPublisher
    }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (event.applicationContext.parent != null) return
        _scope.launch {
            onCreate(event.applicationContext)
            _eventPublisher?.publishEvent(ScanReadyEvent(this@BusinessUnitScanner))
        }
    }
}