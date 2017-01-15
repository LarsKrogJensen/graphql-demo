package se.lars.kutil

import com.google.inject.*
import com.google.inject.binder.*
import com.intapp.vertx.guice.GuiceVertxDeploymentManager
import io.vertx.core.DeploymentOptions
import java.util.logging.Logger
import java.util.ArrayList

public abstract class ModuleBase : AbstractModule() {

    protected inline fun <reified T : Any> bind(): AnnotatedBindingBuilder<T> {
        return this.bind(T::class.java)!!
    }

    inline fun <reified T : Any> AnnotatedBindingBuilder<in T>.to() = to(T::class.java)!!
    fun ScopedBindingBuilder.asSingleton() = `in`(Singleton::class.java)
}

inline fun <reified T : Any> GuiceVertxDeploymentManager.deploy(options: DeploymentOptions) {
    this.deployVerticle(T::class.java, options)
}

inline fun <reified T : Any> GuiceVertxDeploymentManager.deploy() {
    this.deployVerticle(T::class.java)
}

//class GuiceInjectorBuilder() {
//    private val collected = ArrayList<Module> ()
//
//    fun module (config: Binder.()->Any?) : Module = object: Module {
//        override fun configure(binder: Binder?) {
//            binder!!.config()
//        }
//    }
//
//    fun Module.plus() {
//        collected.add(this)
//    }

//    class object {
//        fun injector(config: GuiceInjectorBuilder.() -> Any?) : Injector {
//            val collector = GuiceInjectorBuilder()
//            collector.config()
//            return Guice.createInjector(collector.collected)!!
//        }
//    }
//}

//inline fun <reified T :Any> Binder.bind() = bind(T::class.java)!!


//inline fun <refied T : Any> AnnotatedBindingBuilder<in T>.to() {
//    return to(T::class.java)!!
//}
//
//inline fun <T> AnnotatedBindingBuilder<in T>.toSingleton() = to(T::class.java)!!.asSingleton()
//
//inline fun <T> Injector.getInstance() = getInstance(T::class.java)!!
//
//inline fun <T> Injector.getProvider() = getProvider(T::class.java())!!
//
//inline fun <T,S: Provider<out T>> LinkedBindingBuilder<T>.toProvider() = toProvider(S::class.java)
//
//fun <T> AnnotatedBindingBuilder<T>.toProvider(provider: Injector.()->T) = toProvider(object: Provider<T> {
//    [Inject] val injector : Injector? = null
//
//    override fun get(): T = injector!!.provider()
//})!!

//fun <T> AnnotatedBindingBuilder<T>.toSingletonProvider(provider: Injector.()->T) = toProvider(provider).asSingleton()