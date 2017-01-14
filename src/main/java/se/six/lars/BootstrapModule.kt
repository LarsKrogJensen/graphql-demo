package se.six.lars

import com.google.inject.AbstractModule
import com.google.inject.Binder
import com.google.inject.Scopes
import com.google.inject.Singleton
import com.google.inject.binder.AnnotatedBindingBuilder
import io.vertx.ext.auth.AuthProvider
import se.six.lars.chat.ChatSystemHandler

class BootstrapModule : AbstractModule() {

    override fun configure() {
//        with(binder()) {
//            val bind: AnnotatedBindingBuilder<IMyService> = bind<IMyService>()
//            bind.to2<MyService>()
//        }

        bind(classOf<IMyService>())!!.to(classOf<MyService>())!!.`in`(Scopes.SINGLETON);

        //bind<IMyService>().to<MyService>()
        bind(classOf<IMyService>()).to(classOf<MyService>()).`in` (Singleton::class.java)
        bind(IApiController::class.java).to(ApiController::class.java)
        bind(ISearchController::class.java).to(SearchController::class.java)
        bind(AuthProvider::class.java).to(ApiAuthProvider::class.java)
        bind(GraphQLHandler::class.java)
        bind(ChatSystemHandler::class.java).`in`(Singleton::class.java)
    }
}


inline fun <T> AnnotatedBindingBuilder<in T>.to() = to(javaClass<T>())!!


inline fun <reified T: Any> classOf() : Class<T> {
    return T::class.java
}

/*
inline fun <reified T: Any> AbstractModule.bind(): AnnotatedBindingBuilder<T> {
    return this.bind(T::class.java)
    //return this.bind(IMyService::class.java)
}
*/

private fun <T> AbstractModule.bind(): AnnotatedBindingBuilder<T> {

}

/*
private fun <T> AbstractModule.bind(java: Class<IMyService>): AnnotatedBindingBuilder<T> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
*/

//inline fun <T : Any, reified R : Any> AnnotatedBindingBuilder<T>.to2(): ScopedBindingBuilder {
//    return this.to()
//
//            to(R::class.java)
//}