package se.lars


import io.vertx.core.eventbus.EventBus
import io.vertx.ext.web.RoutingContext
import javax.inject.Inject

class GraphQLHandler
@Inject
constructor(
        apiController: IApiController,
        searchController: ISearchController,
        eventBus: EventBus
) : GraphQLHandlerBase(apiController, searchController, eventBus) {


    override fun handle(routingContext: RoutingContext) {
        executeGraphQL(routingContext.body.toString(), routingContext.user()) {
            routingContext.response()
                    .putHeader("Content-Type", "application/json")
                    .end(it.encode())
        }
    }
}

