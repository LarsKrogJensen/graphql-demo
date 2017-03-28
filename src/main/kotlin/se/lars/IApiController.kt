package se.lars


import io.vertx.ext.auth.jwt.impl.JWTUser
import se.lars.auth.ApiUser
import se.lars.types.Listing
import se.lars.types.OrderBook
import se.lars.types.Organization
import se.lars.types.Quotes
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

interface IApiController {
    fun listing(listingId: String, usr: JWTUser): CompletableFuture<Listing>

    fun organization(organizationId: String, usr: JWTUser): CompletableFuture<Organization>

    fun listingQuotes(listingId: String, usr: JWTUser): CompletableFuture<Quotes>

    fun listingOrderBook(listingId: String, usr: JWTUser): CompletableFuture<OrderBook>

    fun authenticate(clientId: String, clientSecret: String): CompletableFuture<ApiUser>
}

