package io.orbyt.domain.model

data class BusinessUnitInfo(
    val name: String,
    val method: String,
    var hash: String,
    val clazz: String
)

data class BusinessDomain (
    val name: String,
    val businessUnits: MutableList<BusinessUnitInfo>
)