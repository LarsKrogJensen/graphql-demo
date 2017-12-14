package se.lars

import com.google.inject.Guice
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.cli.CLI
import io.vertx.core.cli.CommandLine
import io.vertx.core.cli.Option
import io.vertx.core.json.JsonObject
import io.vertx.ext.dropwizard.DropwizardMetricsOptions
import io.vertx.kotlin.config.ConfigRetrieverOptions
import io.vertx.kotlin.config.ConfigStoreOptions
import se.lars.chat.ChatRoomVerticle
import se.lars.codec.KryoCodec
import se.lars.codec.NoOpCodec
import se.lars.guice.GuiceVerticleFactory
import se.lars.guice.GuiceVertxDeploymentManager
import se.lars.guice.VertxModule
import se.lars.guice.deploy
import se.lars.kutil.jsonObject
import se.lars.kutil.shutdownHook
import se.lars.services.MetricsVerticle
import se.lars.services.SearchVerticle
import java.net.URL

fun main(args: Array<String>) {

    displayBanner()
    configureVertxLogging()

    val commandLine = parseCommandLine(args)
    val configStores = loadConfigStores(commandLine)

    val vertx = Vertx.vertx(VertxOptions().apply {
        metricsOptions = DropwizardMetricsOptions().apply {
            isJmxEnabled = true
            registryName = "Metrics"
        }
    })

    ConfigRetriever.create(vertx, configStores).getConfig { result ->
        if (result.failed()) {
            throw RuntimeException("Failed to read config", result.cause())
        } else {
            val injector = Guice.createInjector(BootstrapModule(result.result(), vertx.eventBus()), VertxModule(vertx))

            vertx.registerVerticleFactory(GuiceVerticleFactory(injector))
            KryoCodec.resolveKryoAwareClasses("se.lars.chat", "se.lars.messages")
                    .forEach { clazz ->
                        vertx.eventBus().registerDefaultCodec(clazz, NoOpCodec(clazz))
//                        vertx.eventBus().registerDefaultCodec(clazz, KryoCodec(clazz))
                    }

            val cores = Runtime.getRuntime().availableProcessors()
            val wsOptions = DeploymentOptions().setInstances(cores)

            GuiceVertxDeploymentManager(vertx).apply {
                deploy<WebServerVerticle>(wsOptions)
                deploy<ChatRoomVerticle>()
                deploy<MetricsVerticle>()
                deploy<SearchVerticle>()
            }

            shutdownHook { completion ->
                vertx.close {
                    completion.complete(Unit)
                }

            }
        }
    }
}

private fun loadConfigStores(commandLine: CommandLine): ConfigRetrieverOptions {
    val config = commandLine.getOptionValue<String>("config")

    val mainStore = if (config == null)
        ConfigStoreOptions(type = "json", config = loadDefaultConfig())
    else
        ConfigStoreOptions(type = "file", format = "yaml", config = jsonObject("path" to config))

    return ConfigRetrieverOptions(stores = listOf(
            mainStore,
            ConfigStoreOptions(type = "sys"),
            ConfigStoreOptions(type = "env")
    ))
}

private fun parseCommandLine(args: Array<String>): CommandLine {
    return CLI.create("server").apply {
        summary = "API Service"

        addOption(Option().apply {
            shortName = "c"
            longName = "config"
            description = "server config"
            isRequired = false
        })
    }.parse(args.toList(), true)
}

private fun configureVertxLogging() {
    System.setProperty("vertx.logger-delegate-factory-class-name",
                       "io.vertx.core.logging.SLF4JLogDelegateFactory")
}

fun displayBanner() {
    val resource: URL = ClassLoader.getSystemClassLoader().getResource("banner.txt")
    println(resource.readText())
}

fun loadDefaultConfig(): JsonObject {
    val resource: URL = ClassLoader.getSystemClassLoader().getResource("dev.json")
    return JsonObject(resource.readText())
}

