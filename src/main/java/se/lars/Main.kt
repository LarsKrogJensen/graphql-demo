package se.lars

import com.google.inject.Guice
import com.google.inject.Injector
import com.intapp.vertx.guice.*
import io.vertx.core.*
import se.lars.chat.*
import se.lars.codec.KryoCodec
import se.lars.kutil.deploy

object Main {
    @Throws(Exception::class)
    @JvmStatic fun main(args: Array<String>) {
        System.setProperty("vertx.logger-delegate-factory-class-name",
                           "io.vertx.core.logging.SLF4JLogDelegateFactory")

        val vertx = Vertx.vertx()

        val injector = Guice.createInjector(BootstrapModule(), VertxModule(vertx))
        vertx.registerVerticleFactory(GuiceVerticleFactory(injector))
        KryoCodec.resolveKryoAwareClasses("se.lars.chat")
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

