package se.lars

import io.vertx.ext.auth.jwt.impl.JWTUser
import se.lars.auth.ApiUser
import se.lars.types.Listing
import se.lars.types.OrderBook
import se.lars.types.Organization
import se.lars.types.Quotes
import java.util.concurrent.CompletableFuture

class MockApiController : IApiController {
    override fun listing(listingId: String, usr: JWTUser): CompletableFuture<Listing> {
        return CompletableFuture.completedFuture(null)
    }

    override fun organization(organizationId: String, usr: JWTUser): CompletableFuture<Organization> {
        return CompletableFuture.completedFuture(null)
    }

    override fun listingQuotes(listingId: String, usr: JWTUser): CompletableFuture<Quotes> {
        return CompletableFuture.completedFuture(null)
    }

    override fun listingOrderBook(listingId: String, usr: JWTUser): CompletableFuture<OrderBook> {
        return CompletableFuture.completedFuture(null)
    }

    override fun authenticate(clientId: String, clientSecret: String): CompletableFuture<ApiUser> {
        return CompletableFuture.completedFuture(null)
    }
}