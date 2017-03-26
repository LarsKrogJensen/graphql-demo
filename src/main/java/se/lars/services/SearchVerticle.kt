package se.lars.services

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.Message
import se.lars.IServerOptions
import se.lars.kutil.documentOf
import se.lars.kutil.loggerFor
import se.lars.kutil.matchOf
import se.lars.messages.SearchQuery
import se.lars.messages.SearchResult
import se.lars.types.SearchItem
import java.net.URLEncoder
import javax.inject.Inject


class SearchVerticle @Inject constructor(options: IServerOptions) : WebClientVericleBase(options) {
    private val log = loggerFor<SearchVerticle>()
    override fun start() {
        super.start()

        vertx.eventBus().consumer<SearchQuery>(SearchQuery::class.java.name, this::searchHandler)
    }

    private fun searchHandler(msg: Message<SearchQuery>) {
        val requestURI = "/dictionary/rest/InsSearch?query=${URLEncoder.encode(msg.body().query, "UTF-8")}&count=100&format=xml&ticket=notused"
        log.info("Query: $requestURI")

        httpClient.get(requestURI)
                .setTimeout(2000)
                .exceptionHandler { ex -> msg.fail(0, ex.message) }
                .handler { response ->
                    if (response.statusCode() == HttpResponseStatus.OK.code()) {
                        response.bodyHandler { buffer ->
                            msg.reply(SearchResult(parseResponse(buffer)))
                        }
                    } else {
                        msg.fail(0, response.statusMessage())
                    }
                }.end()
    }

    private fun parseResponse(buffer: Buffer): MutableList<SearchItem> {
        val result = documentOf(buffer).find("DataRow")
                .map(::matchOf)
                .map { match ->
                    SearchItem(match.child("Id").content().removePrefix("Ts_"), //id
                               match.child("Score").content().toFloat(), //score
                               match.child("Nms").content(), //name
                               match.child("Nm").content()) // longName
                }
                .fold(mutableListOf<SearchItem>()) { list, item ->
                    list += item
                    list
                }
        log.info("Found ${result.size} matches")
        return result
    }
}

