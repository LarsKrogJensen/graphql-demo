package se.lars

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpVersion
import org.slf4j.LoggerFactory
import se.lars.kutil.documentOf
import se.lars.kutil.matchOf
import se.lars.types.SearchItem
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import javax.inject.Inject


class SearchController @Inject
constructor(vertx: Vertx) : ISearchController {
    private val _log = LoggerFactory.getLogger(ApiController::class.java)
    private val _httpClient: HttpClient

    init {

        // Prepare http client options to run HTTP/2
        val options = HttpClientOptions().apply {
            protocolVersion = HttpVersion.HTTP_2
            isSsl = true
            isUseAlpn = true
            isTrustAll = true
            defaultHost = "services.six.se"
            defaultPort = 443
            logActivity = false
            connectTimeout = 1000
        }

        // Http client is thread safe an a single instance is sufficent
        _httpClient = vertx.createHttpClient(options)

    }

    override fun searchListings(query: String, user: ApiUser): CompletionStage<List<SearchItem>> {
        val future = CompletableFuture<List<SearchItem>>()

        val requestURI = "/dictionary/rest/InsSearch?query=${URLEncoder.encode(query,"UTF-8")}&count=100&format=xml&ticket=notused"
        _log.info("Query: $requestURI")

        _httpClient.get(requestURI)
                .setTimeout(2000)
                .exceptionHandler { ex -> future.completeExceptionally(ex) }
                .handler { response ->
                    if (response.statusCode() == HttpResponseStatus.OK.code()) {
                        response.bodyHandler { buffer ->
                            documentOf(buffer).find("DataRow")
                                    .map { matchOf(it) }
                                    .map { match ->
                                        SearchItem(match.child("Id").content().removePrefix("Ts_"), //id
                                                   match.child("Score").content().toFloat(), //score
                                                   match.child("Nms").content(), //name
                                                   match.child("Nm").content()) // longName
                                    }
                                    .fold(mutableListOf<SearchItem>()) { list, item ->
                                        list.add(item)
                                        list
                                    }.complete(future)
                        }
                    } else {
                        future.completeExceptionally(Exception(response.statusMessage()))
                    }
                }.end()

        return future
    }
}


private fun <T> T.complete(future: CompletableFuture<T>) {
    future.complete(this)
}

