package se.lars


import io.vertx.ext.web.RoutingContext
import se.lars.kutil.jsonObject
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
            try {
                executeGraphQL(buffer.toString(), user) {
                    // we have a response for the query
                    ws.writeFinalTextFrame(it.encode())
                }
            } catch (e: Exception) {
               ws.writeFinalTextFrame(jsonObject("errors" to e).encode())
            }
        }.exceptionHandler {

        }
    }
}
