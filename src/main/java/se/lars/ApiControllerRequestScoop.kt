package se.lars


import io.vertx.ext.auth.jwt.impl.JWTUser
import se.lars.auth.ApiUser
import se.lars.types.*
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap


class ApiControllerRequestScoop(private val realApi: IApiController) : IApiController {

    private val _organizationCache = ConcurrentHashMap<String, CompletionStage<Organization>>()
    private val _listingsCache = ConcurrentHashMap<String, CompletionStage<Listing>>()
    private val _quotesCache = ConcurrentHashMap<String, CompletionStage<Quotes>>()
    private val _orderbookCache = ConcurrentHashMap<String, CompletionStage<OrderBook>>()

    override fun listing(listingId: String, usr: JWTUser): CompletionStage<Listing> {
        return _listingsCache.computeIfAbsent(listingId) { key ->
            realApi.listing(key, usr)
        }
    }

    override fun organization(organizationId: String, usr: JWTUser): CompletionStage<Organization> {
        return _organizationCache.computeIfAbsent(organizationId) { key ->
            realApi.organization(key, usr)
        }
    }

    override fun listingQuotes(listingId: String, usr: JWTUser): CompletionStage<Quotes> {
        return _quotesCache.computeIfAbsent(listingId) { key ->
            realApi.listingQuotes(key, usr)
        }
    }

    override fun listingOrderBook(listingId: String, usr: JWTUser): CompletionStage<OrderBook> {
        return _orderbookCache.computeIfAbsent(listingId) { key ->
            realApi.listingOrderBook(key, usr)
        }
    }

    override fun authenticate(clientId: String, clientSecret: String): CompletionStage<ApiUser> {
        return realApi.authenticate(clientId, clientSecret)
    }
}


