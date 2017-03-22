package se.lars.auth

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Future.*
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User
import se.lars.IApiController
import se.lars.kutil.thenOn
import javax.inject.Inject

class ApiAuthProvider
@Inject
constructor(private val _api: IApiController) : AuthProvider {
    override fun authenticate(authInfo: JsonObject, handler: Handler<AsyncResult<User>>) {

        val username = authInfo.getString("username")
        val password = authInfo.getString("password")
        _api.authenticate(username, password)
                .thenOn(Vertx.currentContext())
                .thenAccept { apiUser ->
                    if (apiUser != null)
                        handler.handle(succeededFuture<User>(apiUser))
                    else
                        handler.handle(failedFuture<User>("jksksks"))
                }
    }

}