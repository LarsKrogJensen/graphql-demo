package se.lars

import com.codahale.metrics.MetricRegistry

import com.codahale.metrics.MetricSet
import com.codahale.metrics.SharedMetricRegistries
import com.codahale.metrics.jvm.GarbageCollectorMetricSet
import com.codahale.metrics.jvm.MemoryUsageGaugeSet
import com.codahale.metrics.jvm.ThreadStatesGaugeSet
import com.google.inject.Guice
import io.vertx.config.ConfigRetriever
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.ext.dropwizard.DropwizardMetricsOptions
import io.vertx.kotlin.config.ConfigRetrieverOptions
import io.vertx.kotlin.config.ConfigStoreOptions
import metrics_influxdb.HttpInfluxdbProtocol
import metrics_influxdb.InfluxdbReporter
import se.lars.chat.ChatRoomVerticle
import se.lars.codec.KryoCodec
import se.lars.guice.GuiceVerticleFactory
import se.lars.guice.GuiceVertxDeploymentManager
import se.lars.guice.VertxModule
import se.lars.guice.deploy
import se.lars.kutil.jsonObject
import java.net.URL
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    displayBanner()
    System.setProperty("vertx.logger-delegate-factory-class-name",
                       "io.vertx.core.logging.SLF4JLogDelegateFactory")


    val vertxOptions = VertxOptions().apply {
        metricsOptions = DropwizardMetricsOptions().apply {
            isJmxEnabled = true
            registryName = "Metrics"
        }
    }
    val vertx = Vertx.vertx(vertxOptions)

    val options = ConfigRetrieverOptions(stores = listOf(
            ConfigStoreOptions(type = "file", format = "yaml", config = jsonObject("path" to args[0])),
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
            startReport(SharedMetricRegistries.getOrCreate("Metrics"))
        }
    }


}

fun displayBanner() {
    val resource: URL = ClassLoader.getSystemClassLoader().getResource("banner.txt")
    println(resource.readText())
}

fun startReport(registry: MetricRegistry) {
    registerAll("gc", GarbageCollectorMetricSet(), registry)
    //registerAll("buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()), registry);
    registerAll("memory", MemoryUsageGaugeSet(), registry)
    registerAll("threads", ThreadStatesGaugeSet(), registry)


    InfluxdbReporter.forRegistry(registry)
            .prefixedWith("lars")
            .protocol(HttpInfluxdbProtocol("192.168.1.36"))
            .tag("server", "server-1")
            .build()
            .start(1, TimeUnit.SECONDS)
}

fun registerAll(prefix: String, metricSet: MetricSet, registry: MetricRegistry) {
    for ((key, value) in metricSet.metrics) {
        if (value is MetricSet) {
            registerAll(prefix + "." + key, value, registry)
        } else {
            registry.register(prefix + "." + key, value)
        }
    }
}
