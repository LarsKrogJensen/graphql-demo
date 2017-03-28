package se.lars.types


data class Organization (
    val id: String,
    val name: String?,
    val countryCode: String?,
    val industryClassification: Sector?,
    val subIndustryClassification: Sector?,
    val listedEquities: List<Reference>?,
    val mostLiquidEquity: Reference?
)
