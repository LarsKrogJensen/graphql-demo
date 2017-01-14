package se.six.lars


import graphql.GraphQL
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import se.six.lars.schema.ApiRequestContext
import se.six.lars.schema.schema
import javax.inject.Inject

class GraphQLHandler
@Inject
constructor(private val apiController: IApiController, private val searchController: ISearchController) : Handler<RoutingContext> {


    override fun handle(routingContext: RoutingContext) {
        val user = routingContext.user() as ApiUser
        val graphQL = GraphQL(schema)


        val jsonBody = routingContext.bodyAsJson
        val variables = jsonBody.getJsonObject("variables")?.map ?: emptyMap<String, Any>()
        val query = jsonBody.getString("query")
        val operation: String? = jsonBody.getString("operationName")

        val context = ApiRequestContext(user, ApiControllerRequestScoop(apiController), searchController)

        graphQL.execute(query, operation, context, variables)
                .toCompletableFuture()
                .thenAccept { result ->
                    val errors = result.errors
                    if (errors.isEmpty()) {
                        val data = JsonObject().apply {
                            put("data", result.data)
                        }
                        routingContext.response().end(data.encode())
                    } else {
                        val data = mapOf<String, Any>("errors" to errors)
                        routingContext.response()
                                .end(JsonObject(data).encode())
                    }
                }
    }
}
