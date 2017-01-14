package se.six.lars

import io.vertx.ext.auth.AuthProvider
import se.six.lars.chat.ChatSystemHandler
import se.six.lars.kutil.ModuleBase


class BootstrapModule : ModuleBase() {

    override fun configure() {
        bind<IMyService>().to<MyService>().asSingleton()
        bind<IApiController>().to<ApiController>()
        bind<ISearchController>().to<SearchController>()
        bind<AuthProvider>().to<ApiAuthProvider>()
        bind<GraphQLHandler>()
        bind<ChatSystemHandler>().asSingleton()
    }
}