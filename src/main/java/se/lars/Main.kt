package se.lars

import com.google.inject.Guice
import io.vertx.config.ConfigRetriever
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.kotlin.config.ConfigRetrieverOptions
import io.vertx.kotlin.config.ConfigStoreOptions
import se.lars.chat.ChatRoomVerticle
import se.lars.codec.KryoCodec
import se.lars.guice.GuiceVerticleFactory
import se.lars.guice.GuiceVertxDeploymentManager
import se.lars.guice.VertxModule
import se.lars.guice.deploy
import se.lars.kutil.jsonObject

fun main(args: Array<String>) {
    System.setProperty("vertx.logger-delegate-factory-class-name",
                       "io.vertx.core.logging.SLF4JLogDelegateFactory")

    val vertx = Vertx.vertx()

    val options = ConfigRetrieverOptions(stores = listOf(
            ConfigStoreOptions(type = "file",
                               format = "yaml",
                               config = jsonObject("path" to args[0])),
            ConfigStoreOptions(type = "sys"),
            ConfigStoreOptions(type = "env")
    ))

    ConfigRetriever.create(vertx, options).getConfig { result ->
        if (result.failed()) {
            throw RuntimeException("Failed to read config", result.cause())
        } else {
            val injector = Guice.createInjector(BootstrapModule(result.result()), VertxModule(vertx))

            vertx.registerVerticleFactory(GuiceVerticleFactory(injector))
            KryoCodec.resolveKryoAwareClasses("se.lars.chat")
                    .forEach { vertx.eventBus().registerDefaultCodec(it, KryoCodec(it)) }

            val cores = Runtime.getRuntime().availableProcessors()
            val wsOptions = DeploymentOptions().setInstances(cores)

            GuiceVertxDeploymentManager(vertx).apply {
                deploy<WebServerVerticle>(wsOptions)
                deploy<ChatRoomVerticle>()
            }
        }
    }


}

