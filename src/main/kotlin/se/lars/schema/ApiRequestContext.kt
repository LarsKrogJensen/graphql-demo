package se.lars.schema


import io.vertx.core.eventbus.EventBus
import io.vertx.ext.auth.jwt.impl.JWTUser
import se.lars.IApiController
import se.lars.ISearchController

data class ApiRequestContext(
        val user: JWTUser,
        val apiController: IApiController,
        val searchController: ISearchController,
        val eventBus: EventBus
)

