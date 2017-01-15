package se.lars;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.*;
import org.junit.runner.RunWith;


@RunWith(VertxUnitRunner.class)
public class MyFirstVerticleTest
{
    private Vertx vertx;
    private final int port = 8181;

    @Before
    public void setup(TestContext context)
    {
        vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions()
            .setConfig(new JsonObject().put("http.port", port));
        vertx.deployVerticle(MyFirstVerticle.class.getName(),
                             options,
                             context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context)
    {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testApp(TestContext context)
        throws Exception
    {
        Async async = context.async();
        vertx.createHttpClient()
             .getNow(port,
                     "localhost",
                     "/",
                     response -> {
                         response.handler(body -> {
                             context.assertTrue(body.toString().contains("Hello"));
                             async.complete();
                         });
                     });
    }
}