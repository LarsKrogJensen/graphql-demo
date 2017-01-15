package se.lars


import io.vertx.ext.web.RoutingContext
import se.lars.kutil.cast
import javax.inject.Inject

class GraphQLHandler
@Inject
constructor(apiController: IApiController,
            searchController: ISearchController) : GraphQLHandlerBase(apiController, searchController) {

    override fun handle(routingContext: RoutingContext) {

        executeGraphQL(routingContext.bodyAsJson, routingContext.user().cast<ApiUser>()) {
            routingContext.response().end(it.encode())
        }
    }
}

