package io.orbyt.library.annot

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BusinessUnit(
    val name: String,
    val domain: String
)