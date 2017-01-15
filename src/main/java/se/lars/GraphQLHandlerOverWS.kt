package se.lars


import graphql.GraphQL
import io.vertx.ext.web.RoutingContext
import se.lars.kutil.cast
import se.lars.kutil.jsonObject
import se.lars.schema.ApiRequestContext
import se.lars.schema.schema
import javax.inject.Inject

class GraphQLHandlerOverWS
@Inject
constructor(apiController: IApiController,
            searchController: ISearchController) : GraphQLHandlerBase(apiController, searchController) {

    override fun handle(routingContext: RoutingContext) {
        val user = routingContext.user() as ApiUser
        val ws = routingContext.request().upgrade()

        // configure websocket async callback handler that is
        // invoked on each input frame
        ws.handler { buffer ->
            // received a gql query execute async and supply
            // a async reponse callback handler
            executeGraphQL(buffer.toJsonObject(), user) {
                // we have a response for the query
                ws.writeFinalTextFrame(it.encode())
            }
        }
    }
}
