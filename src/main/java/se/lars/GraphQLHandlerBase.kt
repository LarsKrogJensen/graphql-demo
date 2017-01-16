package se.lars

import graphql.GraphQL
import io.vertx.core.Context
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import se.lars.kutil.jsonObject
import se.lars.kutil.loggerFor
import se.lars.kutil.thenOn
import se.lars.schema.ApiRequestContext
import se.lars.schema.schema
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

abstract class GraphQLHandlerBase(private val apiController: IApiController,
                                  private val searchController: ISearchController) : Handler<RoutingContext> {
    val log = loggerFor<GraphQLHandlerBase>()
    protected fun executeGraphQL(jsonText: String, user: ApiUser, handler: (JsonObject) -> Unit): Unit {

        // be a bit more forgiving
        val body = jsonText.replace('\n', ' ').replace('\t', ' ')

        // Validate json input
        val json: JsonObject = try {
            JsonObject(body)
        } catch(e: Exception) {
            handler(jsonObject("errors" to "Invalid Json format"))
            return
        }

        val graphQL = GraphQL(schema)
        val variables = json.getJsonObject("variables")?.map ?: emptyMap<String, Any>()
        val query = json.getString("query")
        val operation: String? = json.getString("operationName")

        val context = ApiRequestContext(user, ApiControllerRequestScoop(apiController), searchController)

        graphQL.execute(query, operation, context, variables)
                .thenOn(Vertx.currentContext())
                .thenAccept { result ->
                    log.info("Completed")
                    val jsonResponse = if (result.succeeded()) {
                        jsonObject("data" to result.data)
                    } else {
                        jsonObject("errors" to result.errors)
                    }

                    handler(jsonResponse)
                }
    }
}
