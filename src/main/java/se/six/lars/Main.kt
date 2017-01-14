package se.six.lars

import com.google.inject.Guice
import com.google.inject.Injector
import com.intapp.vertx.guice.*
import io.vertx.core.*
import se.six.lars.chat.*
import se.six.lars.codec.KryoCodec

object Main {
    @Throws(Exception::class)
    @JvmStatic fun main(args: Array<String>) {
        System.setProperty("vertx.logger-delegate-factory-class-name",
                           "io.vertx.core.logging.SLF4JLogDelegateFactory")

        val vertx = Vertx.vertx()

        val injector = Guice.createInjector(BootstrapModule(), VertxModule(vertx))
        vertx.registerVerticleFactory(GuiceVerticleFactory(injector))
        KryoCodec.resolveKryoAwareClasses("se.six.lars.chat")
                .map { type -> type }
                .forEach { type ->
                    println("Registering kryocodec " + type)
                    vertx.eventBus().registerDefaultCodec(type, KryoCodec(type))
                }


        val wsOptions = DeploymentOptions().setInstances(8)

        GuiceVertxDeploymentManager(vertx).apply {
            deploy<WebServerVerticle>(wsOptions)
            deploy<ChatRoomVerticle>()
        }
    }
}

inline fun <reified T : Any> GuiceVertxDeploymentManager.deploy(options: DeploymentOptions) {
    this.deployVerticle(T::class.java, options)
}

inline fun <reified T : Any> GuiceVertxDeploymentManager.deploy() {
    this.deployVerticle(T::class.java)
}