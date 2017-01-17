package se.lars.auth

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AbstractUser
import io.vertx.ext.auth.AuthProvider


class ApiUser(private val _principal: JsonObject) : AbstractUser() {


    override fun doIsPermitted(permission: String, resultHandler: Handler<AsyncResult<Boolean>>) = Unit

    override fun principal() = _principal

    override fun setAuthProvider(authProvider: AuthProvider) = Unit
}
