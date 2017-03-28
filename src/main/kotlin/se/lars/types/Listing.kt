package se.lars.types


import java.util.*


data class Listing (
    val id: String,
    val name: String?,
    val type: String?,
    val currencyCode: String?,
    val longName: String?,
    val isinCode: String?,
    val micCode: String?,
    val listingDate: Date?,
    val roundLot: Int?,
    val issuer: Reference?
)
