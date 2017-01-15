package se.lars

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User
import javax.inject.Inject

class ApiAuthProvider
@Inject
constructor(private val _api: IApiController) : AuthProvider {
    override fun authenticate(authInfo: JsonObject, handler: Handler<AsyncResult<User>>) {

        val username = authInfo.getString("username")
        val password = authInfo.getString("password")
        _api.authenticate(username, password)
                .thenAccept { apiUser ->
                    if (apiUser != null)
                        handler.handle(Future.succeededFuture<User>(apiUser))
                    else
                        handler.handle(Future.failedFuture<User>("jksksks"))
                }
    }

}