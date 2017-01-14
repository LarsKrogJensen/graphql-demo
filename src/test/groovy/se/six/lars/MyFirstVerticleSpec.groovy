package se.six.lars

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions


class MyFirstVerticleSpec extends Specification {


    def "Runs hellp"() {
        setup:
        def conditions = new AsyncConditions()
        def vertx = Vertx.vertx();

        when:
        vertx.deployVerticle(
                MyFirstVerticle.class.name,
                new DeploymentOptions().setConfig(new JsonObject(["http.port": port])))

        then:
        vertx.createHttpClient().getNow(port, "localhost", "/") { resp ->
            resp.bodyHandler({ result ->
                conditions.evaluate() {
                    assert result.toString().contains("Hello")
                }
            })
        }

        conditions.await()

        cleanup:
        vertx.close()

        where:
        port = 8181


    }
}
