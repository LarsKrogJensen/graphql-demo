package se.lars.services

import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpVersion
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.ext.web.client.WebClientOptions
import se.lars.IServerOptions

abstract class WebClientVericleBase(val options: IServerOptions) : AbstractVerticle() {

    protected lateinit var httpClient: WebClient

    override fun start() {

        // Prepare http client options to run HTTP/2
        val options = WebClientOptions(
            protocolVersion = HttpVersion.HTTP_2,
            ssl = true,
            useAlpn = true,
            trustAll = false,
            defaultHost = "services.six.se",
            defaultPort = 443,
            logActivity = false,
            connectTimeout = 1000
        )

        httpClient = WebClient.create(vertx, options)
    }


}