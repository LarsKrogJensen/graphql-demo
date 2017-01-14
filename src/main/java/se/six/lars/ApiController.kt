package se.six.lars


import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND
import io.netty.handler.codec.http.HttpResponseStatus.OK
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpVersion
import org.slf4j.LoggerFactory
import se.six.lars.kutil.jsonObject
import se.six.lars.types.Listing
import se.six.lars.types.OrderBook
import se.six.lars.types.Organization
import se.six.lars.types.Quotes
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import javax.inject.Inject

class ApiController
@Inject
constructor(_vertx: Vertx) : IApiController {
    private val _log = LoggerFactory.getLogger(ApiController::class.java)
    private val _httpClient: HttpClient
    private val _mapper: ObjectMapper

    init {

        // Prepare http client options to run HTTP/2
        val options = HttpClientOptions().apply {
            protocolVersion = HttpVersion.HTTP_2
            isSsl = true
            isUseAlpn = true
            isTrustAll = true
            defaultHost = "api.six.se"
            defaultPort = 443
            logActivity = false
            connectTimeout = 1000
        }

        // Http client is thread safe an a single instance is sufficent
        _httpClient = _vertx.createHttpClient(options)

        // Create a json deserializer and hint it to ingore unknown properties
        _mapper = ObjectMapper().apply {
            registerModule(GuavaModule())
            registerModule(Jdk8Module())
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

    }

    override fun listing(listingId: String, usr: ApiUser): CompletionStage<Listing> {
        return invokeQuery("/v2/listings/" + listingId, Listing::class.java, usr)
    }

    override fun organization(organizationId: String, usr: ApiUser): CompletionStage<Organization> {
        return invokeQuery("/v2/organizations/" + organizationId, Organization::class.java, usr)
    }

    override fun listingQuotes(listingId: String, usr: ApiUser): CompletionStage<Quotes> {
        return invokeQuery("/v2/listings/$listingId/quotes", Quotes::class.java, usr)
    }

    override fun listingOrderBook(listingId: String, usr: ApiUser): CompletionStage<OrderBook> {
        return invokeQuery("/v2/listings/$listingId/orderbook", OrderBook::class.java, usr)
    }

    override fun authenticate(clientId: String, clientSecret: String): CompletionStage<ApiUser> {
        val future = CompletableFuture<ApiUser>()

        val requestBody = jsonObject("client_id" to clientId,
                                     "client_secret" to clientSecret).encode()

        _log.info("Authenticating {}:{}", clientId, clientSecret)

        _httpClient.post("/v2/authorization/token")
                .exceptionHandler { future.completeExceptionally(it) }
                .handler { response ->
                    response.bodyHandler { buffer ->
                        future.complete(ApiUser(buffer.toJsonObject()))
                    }
                }
                .putHeader("Content-Length", Integer.toString(requestBody.length))
                .putHeader("Content-Type", "application/json")
                .write(requestBody)
                .end()

        return future
    }


    private fun <T> invokeQuery(query: String, type: Class<T>, user: ApiUser): CompletableFuture<T> {
        val future = CompletableFuture<T>()

        val authHeader = with(user.principal()) {
            "${getString("token_type")} ${getString("access_token")}"
        }

        _log.info("Query: https://api.six.se$query")

        _httpClient.get(query)
                .setTimeout(2000)
                .putHeader("authorization", authHeader)
                .exceptionHandler { ex -> future.completeExceptionally(ex) }
                .handler { response ->
                    if (response.statusCode() == OK.code()) {
                        response.bodyHandler { buffer ->
                            try {
                                val typeObj = _mapper.readValue(buffer.bytes, type)
                                future.complete(typeObj)
                            } catch(e: Exception) {
                                _log.error("Failed to invoke https://api.six.se$query", e)
                                future.completeExceptionally(e)
                            }
                        }
                        response.exceptionHandler { e -> future.completeExceptionally(e) }
                    } else if (response.statusCode() == NOT_FOUND.code()) {
                        _log.warn("Not found: https://api.six.se$query")
                        future.complete(null)
                    } else {
                        _log.error("Failed to invoke https://api.six.se$query status code ${response.statusCode()}")
                        future.completeExceptionally(Exception(response.statusMessage()))
                    }
                }.end()
        return future
    }
}
