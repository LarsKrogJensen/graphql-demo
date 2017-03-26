package se.lars.auth

import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTOptions
import io.vertx.ext.web.RoutingContext
import se.lars.IApiController
import se.lars.kutil.jsonObject
import se.lars.kutil.thenOn


class JWTAuthenticator(private val api: IApiController, private val authProvider: JWTAuth) : Handler<RoutingContext> {
    override fun handle(rc: RoutingContext) {

        val clientId = rc.bodyAsJson.getString("client_id")
        val clientSecret = rc.bodyAsJson.getString("client_secret")
        api.authenticate(clientId, clientSecret)
                .thenOn(rc.vertx().orCreateContext)
                .thenAccept { user ->
                    var jwtJson = JsonObject()
                    val json = user.principal()

                    val accessToken = json.getString("access_token")
                    if (accessToken != null) {
                        // put the backend token in the payload
                        val jwtToken = authProvider.generateToken(jsonObject("sub" to accessToken), JWTOptions())
                        jwtJson.put("access_token", jwtToken)

                    } else {
                       jwtJson = json
                    }

                    rc.response().end(jwtJson.encode())
                }
    }
}
