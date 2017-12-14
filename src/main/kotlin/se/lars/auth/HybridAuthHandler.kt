package se.lars.auth

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BasicAuthHandler
import io.vertx.ext.web.handler.impl.AuthHandlerImpl
import se.lars.kutil.jsonObject
import java.util.*

class HybridAuthHandler(authProvider: AuthProvider, private val realm2: String) : AuthHandlerImpl(authProvider) {
    override fun parseCredentials(context: RoutingContext?, handler: Handler<AsyncResult<JsonObject>>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handle(context: RoutingContext) {
//        context.setUser(ApiUser(JsonObject()))
//        context.next()
        val user = context.user()
        if (user != null) {
            // Already authenticated in, just authorise
            //authorise(user, context)
            context.next()
        } else {

            // 1. Check for bearer token
            handleBearerToken(context) {
                // 2. next up to try is basic auth
                handleBasicAuth(it) {
                    // nope basic auth was not available
                    // 3. try usr/pwd
                    handleParams(it) {
                        // not available
                        handle401(context)
                    }
                }
            }
        }
    }

    private fun handleBearerToken(context: RoutingContext, next: (RoutingContext) -> Unit) {
        // not implemented, forward
        next(context)
    }

    private fun handleBasicAuth(context: RoutingContext, next: (RoutingContext) -> Unit) {
        val authorization = context.request().headers().get(HttpHeaders.AUTHORIZATION)

        if (authorization == null) {
            //            handle401(context)
            // no basic auth header
            next(context)
        } else {
            val username: String
            val password: String?
            val scheme: String

            try {
                val parts = authorization.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                scheme = parts[0]
                val decoded = String(Base64.getDecoder().decode(parts[1]))
                val colonIdx = decoded.indexOf(":")
                if (colonIdx != -1) {
                    username = decoded.substring(0, colonIdx)
                    password = decoded.substring(colonIdx + 1)
                } else {
                    username = decoded
                    password = null
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
                handle401(context)
                return
            } catch (e: IllegalArgumentException) {
                // IllegalArgumentException includes PatternSyntaxException
                context.fail(e)
                return
            } catch (e: NullPointerException) {
                context.fail(e)
                return
            }

            if ("Basic" != scheme || password == null) {
                context.fail(400)
            } else {
                authenticate(context, username, password)
            }
        }
    }

    private fun handleParams(context: RoutingContext, next: (RoutingContext) -> Unit) {
        val usr : String? = context.request().getParam("usr")
        val pwd : String? = context.request().getParam("pwd")
        if (usr != null && pwd != null)
            authenticate(context, usr, pwd)
        else
            next(context)
    }

    private fun authenticate(context: RoutingContext, username: String, password: String) {
        val authInfo = jsonObject("username" to username,
                                  "password" to password)
        authProvider.authenticate(authInfo) { res ->
            if (res.succeeded()) {
                val authenticated = res.result()
                context.setUser(authenticated)
                //authorise(authenticated, context)
                context.next()
            } else {
                handle401(context)
            }
        }
    }

    private fun handle401(context: RoutingContext) {
        context.response().putHeader("WWW-Authenticate", "Basic realm=\"" + this.realm2 + "\"")
        context.fail(401)
    }

    companion object {
        fun create(provider: AuthProvider, realm: String = BasicAuthHandler.DEFAULT_REALM) = HybridAuthHandler(provider, realm)
    }
}