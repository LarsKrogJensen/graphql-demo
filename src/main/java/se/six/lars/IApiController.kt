package se.six.lars


import se.six.lars.types.*

import java.util.concurrent.CompletionStage

interface IApiController {
    fun listing(listingId: String, usr: ApiUser): CompletionStage<Listing>

    fun organization(organizationId: String, usr: ApiUser): CompletionStage<Organization>

    fun listingQuotes(listingId: String, usr: ApiUser): CompletionStage<Quotes>

    fun listingOrderBook(listingId: String, usr: ApiUser): CompletionStage<OrderBook>

    fun authenticate(clientId: String, clientSecret: String): CompletionStage<ApiUser>
}

