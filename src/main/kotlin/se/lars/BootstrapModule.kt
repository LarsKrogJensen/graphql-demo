package se.lars

import com.google.inject.name.Names.named
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import se.lars.auth.ApiAuthProvider
import se.lars.chat.ChatSystemHandler
import se.lars.guice.ModuleBase
import se.lars.services.MetricsVerticle


class BootstrapModule(private val config: JsonObject, private val eventBus: EventBus) : ModuleBase() {

    override fun configure() {
        bind(JsonObject::class.java).annotatedWith(named("config")).toInstance(config)
        bind<IServerOptions>().to<ServerOptions>().asSingleton()
        bind<IMyService>().to<MyService>().asSingleton()
        bind<IApiController>().to<ApiController>()
        bind<ISearchController>().to<SearchController>()
        bind<AuthProvider>().to<ApiAuthProvider>()
        bind<GraphQLHandler>()
        bind<GraphQLHandler>().annotatedWith(named("mock")).toInstance(GraphQLHandler(MockApiController(),MockSearchController(), eventBus))
        bind<GraphQLHandlerOverWS>()
        bind<GraphQLHandlerOverWS>().annotatedWith(named("mock")).toInstance(GraphQLHandlerOverWS(MockApiController(), MockSearchController(), eventBus))
        bind<ChatSystemHandler>().asSingleton()
        bind<MetricsVerticle>().asEagerSingleton()
    }
}