package se.lars

import graphql.GraphQL
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import se.lars.kutil.jsonObject
import se.lars.schema.ApiRequestContext
import se.lars.schema.schema

abstract class GraphQLHandlerBase(private val apiController: IApiController,
                                  private val searchController: ISearchController) : Handler<RoutingContext> {

    protected fun executeGraphQL(json: JsonObject, user: ApiUser, handler: (JsonObject) -> Unit): Unit {
        val graphQL = GraphQL(schema)
        val variables = json.getJsonObject("variables")?.map ?: emptyMap<String, Any>()
        val query = json.getString("query")
        val operation: String? = json.getString("operationName")

        val context = ApiRequestContext(user, ApiControllerRequestScoop(apiController), searchController)

        graphQL.execute(query, operation, context, variables)
                .toCompletableFuture()
                .thenAccept { result ->
                    val jsonResponse = if (result.succeeded()) {
                        jsonObject("data" to result.data)
                    } else {
                        jsonObject("errors" to result.errors)
                    }

                    handler(jsonResponse)
                }
    }
}