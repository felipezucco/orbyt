package io.orbyt.domain.annot

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableOrbit(
    val basePackages: Array<String> = []
)
