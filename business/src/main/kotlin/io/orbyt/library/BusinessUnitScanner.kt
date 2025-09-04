package io.orbyt.library

import io.orbyt.domain.annot.EnableOrbit
import io.orbyt.domain.model.BusinessRegistry
import io.orbyt.domain.model.BusinessUnitInfo
import io.orbyt.library.annot.BusinessUnit
import io.orbyt.library.port.out.CommunicationRegistry
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.type.filter.AssignableTypeFilter
import java.security.MessageDigest
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

class BusinessUnitScanner(
    private val registry: CommunicationRegistry,
    private val context: ApplicationContext
) {

    init {
        onCreate()
    }

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

    fun onCreate() {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AssignableTypeFilter(Any::class.java))

        findRootPackages().let { pkg -> pkg.forEach { this.scan(scanner, it) }}
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
        (registry as BusinessRegistry).start()
    }

    private fun findRootPackages(): Array<String> {
        val mainBeanName = context.getBeanNamesForAnnotation(EnableOrbit::class.java)
            .firstOrNull()
            ?: throw IllegalArgumentException("EnableOrbit annotation not found")

        val mainClass = context.getType(mainBeanName)
            ?: throw IllegalArgumentException("Cannot determine class for bean $mainBeanName")

        // busca recursiva, incluindo meta-anotações
        val annotation = AnnotatedElementUtils.findMergedAnnotation(mainClass, EnableOrbit::class.java)
            ?: throw IllegalArgumentException("@EnableSolar annotation not present on class $mainClass")

        return annotation.basePackages
    }


}
