package se.lars.types


import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.immutables.value.Value


data class OrderLevel(
        val level: Int,
        val askPrice: Double,
        val bidPrice: Double,
        val askVolume: Double,
        val bidVolume: Double,
        val askOrders: Double,
        val bidOrders: Double)


