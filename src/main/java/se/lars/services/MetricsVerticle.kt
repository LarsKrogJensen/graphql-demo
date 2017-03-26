package se.lars.services

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.MetricSet
import com.codahale.metrics.ScheduledReporter
import com.codahale.metrics.SharedMetricRegistries
import com.codahale.metrics.jvm.GarbageCollectorMetricSet
import com.codahale.metrics.jvm.MemoryUsageGaugeSet
import com.codahale.metrics.jvm.ThreadStatesGaugeSet
import io.vertx.core.AbstractVerticle
import metrics_influxdb.HttpInfluxdbProtocol
import metrics_influxdb.InfluxdbReporter
import se.lars.IServerOptions
import se.lars.kutil.loggerFor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MetricsVerticle @Inject constructor(val options: IServerOptions) : AbstractVerticle() {
    private val log = loggerFor<MetricsVerticle>()
    private var scheduledReporter: ScheduledReporter? = null


    override fun start() {
        log.info("Metrics service started, reporter is ${options.enableMetrics}")
        if (options.enableMetrics) {
            val registry = SharedMetricRegistries.getOrCreate("Metrics")
            registerAll("gc", GarbageCollectorMetricSet(), registry)
            //registerAll("buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()), registry);
            registerAll("memory", MemoryUsageGaugeSet(), registry)
            registerAll("threads", ThreadStatesGaugeSet(), registry)


            val scheduledReporter = InfluxdbReporter.forRegistry(registry)
                    .prefixedWith("lars")
                    .protocol(HttpInfluxdbProtocol("192.168.1.36"))
                    .tag("server", "server-1")
                    .build()

            scheduledReporter.start(1, TimeUnit.SECONDS)
        }
    }

    override fun stop() {
        scheduledReporter?.stop()
        log.info("Metrics service stopped")
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

}
