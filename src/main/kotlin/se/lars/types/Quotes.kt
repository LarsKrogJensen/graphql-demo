package se.lars.types



import java.util.*

data class Quotes(val lastUpdated: Date?,
                  val openPrice: Double?,
                  val lastPrice: Double?,
                  val askPrice: Double?,
                  val bidPrice: Double?,
                  val highPrice: Double?,
                  val lowPrice: Double?,
                  val tradedVolume: Double?,
                  val tradedAmount: Double? )


