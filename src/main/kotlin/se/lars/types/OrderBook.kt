package se.lars.types


import java.util.*


data class OrderBook(val lastUpdated: Date,
                     val state: String,
                     val levels: List<OrderLevel>)
