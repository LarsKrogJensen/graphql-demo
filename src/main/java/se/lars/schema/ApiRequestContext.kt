package se.lars.schema


import io.vertx.ext.auth.User
import io.vertx.ext.auth.jwt.impl.JWTUser
import se.lars.auth.ApiUser
import se.lars.IApiController
import se.lars.ISearchController
import se.lars.SearchController

data class ApiRequestContext(val user: JWTUser,
                             val apiController: IApiController,
                             val searchController: ISearchController)

