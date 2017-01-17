package se.lars

import io.vertx.ext.auth.AuthProvider
import se.lars.auth.ApiAuthProvider
import se.lars.chat.ChatSystemHandler
import se.lars.kutil.ModuleBase


class BootstrapModule : ModuleBase() {

    override fun configure() {
        bind<IMyService>().to<MyService>().asSingleton()
        bind<IApiController>().to<ApiController>()
        bind<ISearchController>().to<SearchController>()
        bind<AuthProvider>().to<ApiAuthProvider>()
        bind<GraphQLHandler>()
        bind<GraphQLHandlerOverWS>()
//        bind<JWTAuthenticator>()
        bind<ChatSystemHandler>().asSingleton()
    }
}