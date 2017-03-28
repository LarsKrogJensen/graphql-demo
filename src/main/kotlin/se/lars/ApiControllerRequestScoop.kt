package se.lars


import io.vertx.ext.auth.jwt.impl.JWTUser
import se.lars.auth.ApiUser
import se.lars.types.Listing
import se.lars.types.OrderBook
import se.lars.types.Organization
import se.lars.types.Quotes
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap


class ApiControllerRequestScoop(private val realApi: IApiController) : IApiController {

    private val _organizationCache = ConcurrentHashMap<String, CompletableFuture<Organization>>()
    private val _listingsCache = ConcurrentHashMap<String, CompletableFuture<Listing>>()
    private val _quotesCache = ConcurrentHashMap<String, CompletableFuture<Quotes>>()
    private val _orderbookCache = ConcurrentHashMap<String, CompletableFuture<OrderBook>>()

    override fun listing(listingId: String, usr: JWTUser): CompletableFuture<Listing> {
        val cached = _listingsCache[listingId]
        if (cached != null && cached.isCompletedExceptionally) {
            _listingsCache.remove(listingId)
        }
        return _listingsCache.computeIfAbsent(listingId) { key ->
            realApi.listing(key, usr)
        }
    }

    override fun organization(organizationId: String, usr: JWTUser): CompletableFuture<Organization> {
        return _organizationCache.computeIfAbsent(organizationId) { key ->
            realApi.organization(key, usr)
        }
    }

    override fun listingQuotes(listingId: String, usr: JWTUser): CompletableFuture<Quotes> {
        return _quotesCache.computeIfAbsent(listingId) { key ->
            realApi.listingQuotes(key, usr)
        }
    }

    override fun listingOrderBook(listingId: String, usr: JWTUser): CompletableFuture<OrderBook> {
        return _orderbookCache.computeIfAbsent(listingId) { key ->
            realApi.listingOrderBook(key, usr)
        }
    }

    override fun authenticate(clientId: String, clientSecret: String): CompletableFuture<ApiUser> {
        return realApi.authenticate(clientId, clientSecret)
    }
}


