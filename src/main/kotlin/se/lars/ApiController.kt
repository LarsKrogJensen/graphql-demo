package se.lars


import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.Vertx
import io.vertx.core.http.HttpVersion
import io.vertx.core.impl.NoStackTraceThrowable
import io.vertx.ext.auth.jwt.impl.JWTUser
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import org.slf4j.LoggerFactory
import se.lars.auth.ApiUser
import se.lars.kutil.jsonObject
import se.lars.types.Listing
import se.lars.types.OrderBook
import se.lars.types.Organization
import se.lars.types.Quotes
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class ApiController
@Inject
constructor(_vertx: Vertx) : IApiController {
    private val log = LoggerFactory.getLogger(ApiController::class.java)
    private val httpClient: WebClient
    private val mapper: ObjectMapper

    init {

        // Prepare http client options to run HTTP/2
        val options = WebClientOptions().apply {
            protocolVersion = HttpVersion.HTTP_2
            isSsl = true
            isUseAlpn = true
            defaultHost = "api.six.se"
            defaultPort = 443
            logActivity = false
            connectTimeout = 1000
        }

        // Http client is thread safe an a single instance is sufficent
        httpClient = WebClient.create(_vertx, options)

        // Create a json deserializer and hint it to ingore unknown properties
        mapper = ObjectMapper().apply {
            registerModule(GuavaModule())
            registerModule(Jdk8Module())
            registerModule(KotlinModule())
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

    }

    override fun listing(listingId: String, usr: JWTUser): CompletableFuture<Listing> {
        return invokeQuery("/v2/listings/" + listingId, Listing::class.java, usr)
    }

    override fun organization(organizationId: String, usr: JWTUser): CompletableFuture<Organization> {
        return invokeQuery("/v2/organizations/" + organizationId, Organization::class.java, usr)
    }

    override fun listingQuotes(listingId: String, usr: JWTUser): CompletableFuture<Quotes> {
        return invokeQuery("/v2/listings/$listingId/quotes", Quotes::class.java, usr)
    }

    override fun listingOrderBook(listingId: String, usr: JWTUser): CompletableFuture<OrderBook> {
        return invokeQuery("/v2/listings/$listingId/orderbook", OrderBook::class.java, usr)
    }

    override fun authenticate(clientId: String, clientSecret: String): CompletableFuture<ApiUser> {
        val future = CompletableFuture<ApiUser>()

        val requestBody = jsonObject("client_id" to clientId,
                                     "client_secret" to clientSecret).encode()

        log.info("Authenticating {}:{}", clientId, clientSecret)

        httpClient.post("/v2/authorization/token")
            .sendJson(requestBody) { reply ->
                if (reply.succeeded()) {
                    val response = reply.result()
                    if (response.statusCode() == 200) {
                        log.info("Response protocol ${response.version()}")
                        future.complete(ApiUser(response.body().toJsonObject()))
                    } else {
                        future.completeExceptionally(NoStackTraceThrowable("Invalid resp code ${response.statusCode()}"))
                    }
                } else {
                    future.completeExceptionally(reply.cause())
                }

            }

        return future
    }

    private fun <T> invokeQuery(query: String, type: Class<T>, user: JWTUser): CompletableFuture<T> {
        val future = CompletableFuture<T>()

        val authHeader = "Bearer ${user.principal().getString("sub")}"

        log.info("Query: https://api.six.se$query")

        httpClient.get(query)
            .timeout(2000)
            .putHeader("authorization", authHeader)
            .send { reply ->
                if (reply.succeeded()) {
                    val response = reply.result()
                    if (response.statusCode() == 200) {

                        log.info("Response protocol ${response.version()}")
                        try {
                            val typeObj = mapper.readValue(response.body().bytes, type)
                            future.complete(typeObj)
                        } catch (e: Exception) {
                            log.error("Failed to invoke https://api.six.se$query", e)
                            future.completeExceptionally(e)
                        }
                    } else {
                        log.warn("Not found: https://api.six.se$query")
                        future.complete(null)
                    }

                } else {
                    log.error("Failed to invoke https://api.six.se$query ${reply.cause().message}")
                    future.completeExceptionally(reply.cause())
                }
            }
        return future
    }
}
