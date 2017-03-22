package se.lars


import io.vertx.ext.web.RoutingContext
import javax.inject.Inject

class GraphQLHandler
@Inject
constructor(apiController: IApiController,
            searchController: ISearchController) : GraphQLHandlerBase(apiController, searchController) {


    override fun handle(routingContext: RoutingContext) {
        executeGraphQL(routingContext.body.toString(), routingContext.user()) {
            routingContext.response()
                    .putHeader("Content-Type", "application/json")
                    .end(it.encode())
        }
    }
}

