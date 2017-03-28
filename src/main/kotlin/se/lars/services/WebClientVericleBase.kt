package se.lars.services

import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpVersion
import se.lars.IServerOptions

abstract class WebClientVericleBase(val options: IServerOptions) : AbstractVerticle() {

    protected lateinit var httpClient: HttpClient

    override fun start() {

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

        httpClient = vertx.createHttpClient(options)
    }



}