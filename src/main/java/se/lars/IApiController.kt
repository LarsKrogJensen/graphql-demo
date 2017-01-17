package se.lars


import io.vertx.ext.auth.jwt.impl.JWTUser
import se.lars.auth.ApiUser
import se.lars.types.*

import java.util.concurrent.CompletionStage

interface IApiController {
    fun listing(listingId: String, usr: JWTUser): CompletionStage<Listing>

    fun organization(organizationId: String, usr: JWTUser): CompletionStage<Organization>

    fun listingQuotes(listingId: String, usr: JWTUser): CompletionStage<Quotes>

    fun listingOrderBook(listingId: String, usr: JWTUser): CompletionStage<OrderBook>

    fun authenticate(clientId: String, clientSecret: String): CompletionStage<ApiUser>
}

