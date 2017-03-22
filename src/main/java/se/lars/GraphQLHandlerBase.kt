package se.lars

import graphql.newGraphQL
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.jwt.impl.JWTUser
import io.vertx.ext.web.RoutingContext
import se.lars.kutil.cast
import se.lars.kutil.jsonObject
import se.lars.kutil.loggerFor
import se.lars.kutil.thenOn
import se.lars.schema.ApiRequestContext
import se.lars.schema.ConsoleInstrumentation
import se.lars.schema.marketDataSchema
import java.nio.charset.Charset

abstract class GraphQLHandlerBase(private val apiController: IApiController,
                                  private val searchController: ISearchController) : Handler<RoutingContext> {
    val log = loggerFor<GraphQLHandlerBase>()
    private val invalidUser = JWTUser(jsonObject(), "")

    protected fun executeGraphQL(jsonText: String, user: User?, handler: (JsonObject) -> Unit): Unit {
        log.info("Query: \n" + jsonText)
        log.info("Charset: ${Charset.defaultCharset().name()}")
        // be a bit more forgiving
        val body = jsonText.replace('\n', ' ').replace('\t', ' ')

        // Validate json input
        val json: JsonObject = try {
            JsonObject(body)
        } catch(e: Exception) {
            handler(jsonObject("errors" to "Invalid Json format"))
            return
        }


        val graphQL = newGraphQL {
            schema = marketDataSchema
            instrumentation = ConsoleInstrumentation()
        }

        val variables = json.getValue("variables").let {
            when (it) {
                is JsonObject -> it.map
                else          -> emptyMap<String, Any>()
            }
        }

        val query = json.getString("query")
        val operation: String? = json.getString("operationName")

        val context = ApiRequestContext(user?.cast<JWTUser>() ?: invalidUser,
                                        ApiControllerRequestScoop(apiController),
                                        searchController)

        graphQL.execute(query, operation, context, variables)
                .thenOn(Vertx.currentContext())
                .thenAccept { result ->
                    val jsonResponse = if (result.succeeded()) {
                        jsonObject("data" to result.data())
                    } else {
                        jsonObject("errors" to result.errors)
                    }
                    handler(jsonResponse)
                }
    }
}

