package se.lars.types


data class OrderLevel(
        val level: Int,
        val askPrice: Double,
        val bidPrice: Double,
        val askVolume: Double,
        val bidVolume: Double,
        val askOrders: Double,
        val bidOrders: Double)


