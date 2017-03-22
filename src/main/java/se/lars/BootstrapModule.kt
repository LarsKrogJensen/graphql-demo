package se.lars

import com.google.inject.name.Names
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import se.lars.auth.ApiAuthProvider
import se.lars.chat.ChatSystemHandler
import se.lars.guice.ModuleBase


class BootstrapModule(private val config: JsonObject) : ModuleBase() {

    override fun configure() {
        bind(JsonObject::class.java).annotatedWith(Names.named("config")).toInstance(config)
        bind<IMyService>().to<MyService>().asSingleton()
        bind<IApiController>().to<ApiController>()
        bind<ISearchController>().to<SearchController>()
        bind<AuthProvider>().to<ApiAuthProvider>()
        bind<GraphQLHandler>()
        bind<GraphQLHandlerOverWS>()
        bind<ChatSystemHandler>().asSingleton()
    }
}