package se.lars

import io.vertx.core.*
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.net.PemKeyCertOptions
import io.vertx.core.shareddata.SharedData
import io.vertx.ext.auth.*
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.impl.JWTUser
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.*
import io.vertx.ext.web.handler.impl.StaticHandlerImpl
import io.vertx.ext.web.sstore.LocalSessionStore
import io.vertx.rx.java.RxHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.lars.accesslog.AccessLogHandler
import se.lars.chat.ChatSystemHandler
import se.lars.kutil.loggerFor
import se.lars.kutil.router

import javax.inject.Inject
import java.util.Objects
import java.util.Optional


class WebServerVerticle
@Inject
constructor(private val _graphQLHandler: GraphQLHandler,
            private val _graphQLHandlerWs: GraphQLHandlerOverWS,
            private val _chatHandler: ChatSystemHandler,
            private val _authProvider: AuthProvider) : AbstractVerticle() {
    private val _log = loggerFor<WebServerVerticle>()

    @Throws(Exception::class)
    override fun start(startFuture: Future<Void>) {
        val options = HttpServerOptions().apply {
            isCompressionSupported = true
            /* isUseAlpn = true
             isSsl = true
             setPemKeyCertOptions(PemKeyCertOptions().apply {
                 keyPath = "tls/server-key.pem"se
                 certPath = "tls/server-cert.pem"
             })*/
        }

        // configure cross domain access
        val corsHandler = with(CorsHandler.create("*")) {
            allowCredentials(true)
            allowedMethod(HttpMethod.POST)
            allowedHeaders(setOf("content-type","authorization"))
        }

        val router = router(vertx) {
            route().handler(AccessLogHandler.create("%r %s \"%{Content-Type}o\" %D %T %B"))
            route().handler(corsHandler)
            route().handler(BodyHandler.create())
            route().handler(CookieHandler.create())
            route().handler(SessionHandler.create(LocalSessionStore.create(vertx)))
            route().handler(UserSessionHandler.create(_authProvider))
            route().handler(HybridAuthHandler.create(_authProvider))
            route("/chat").handler(_chatHandler)
            route("/graphql").handler(_graphQLHandler)
            route("/graphqlws").handler(_graphQLHandlerWs)

            route("/*").handler(StaticHandler.create().setCachingEnabled(false))
        }

        vertx.createHttpServer(options)
                .requestHandler { router.accept(it) }
                .listen(System.getenv("PORT")?.toInt() ?: 8080) {
                    when (it.succeeded()) {
                        true  -> {
                            _log.info("Http service started. on port " + it.result().actualPort())
                            startFuture.succeeded()
                        }
                        false -> {
                            _log.info("Http service failed to started.")
                            startFuture.fail(it.cause())
                        }
                    }
                }

    }
}

//router.route("/hello")
//      .produces("application/json")
//      .handler(rc -> {
//          System.out.println("HttpVersion: " + rc.request().version());
//          System.out.println("Handling hello on thread: " + Thread.currentThread().getName());
//          _sharedData.getCounter("counter", event -> {
//              System.out.println("Handling counter on thread: " + Thread.currentThread().getName());
//              //Counter counter = event.result();
//              //counter.addAndGet(1, res -> {
//              //    System.out.println("Handling counterAdd on thread: " + Thread.currentThread().getName() + ", counter: " + res.result());
//              //    rc.response()
//              //      .end(new JsonObject().put("greeting", "Hello World").encode());
//              //});
//
//              //_service.getData()
//              //    .whenComplete((stream, throwable) -> {
//              //       _vertx.getOrCreateContext().runOnContext(event1 -> {
//              //           System.out.println("Is on event loop thread: "+ Context.isOnEventLoopThread());
//              //           System.out.println("Is on vertx thread: "+ Context.isOnVertxThread());
//              //           System.out.println("Is on worker context: "+ Context.isOnWorkerThread());
//              //           System.out.println("Completed hello on thread: " + Thread.currentThread().getName());
//              //
//              //           rc.response().end(stream.collect(JsonArray::new, JsonArray::add, JsonArray::addAll).encode());
//              //       });
//              //    });
//
//              //_service.getData2()
//              //        .collect(JsonArray::new, JsonArray::add)
//              //        .map(JsonArray::encode)
//              //        .observeOn(RxHelper.scheduler(_vertx.getOrCreateContext()))
//              //        .subscribe(json -> {
//              //            System.out.println("Is on event loop thread: " + Context.isOnEventLoopThread());
//              //            System.out.println("Is on vertx thread: " + Context.isOnVertxThread());
//              //            System.out.println("Is on worker context: " + Context.isOnWorkerThread());
//              //            System.out.println("Completed hello on thread: " + Thread.currentThread().getName());
//              //
//              //            rc.response().end(json);
//              //        });
//
//              _service.getData3()
//                      .observeOn(RxHelper.scheduler(_vertx.getOrCreateContext()))
//                      .subscribe(json -> {
//                          System.out.println("Is on event loop thread: " + Context.isOnEventLoopThread());
//                          System.out.println("Is on vertx thread: " + Context.isOnVertxThread());
//                          System.out.println("Is on worker context: " + Context.isOnWorkerThread());
//                          System.out.println("Completed hello on thread: " + Thread.currentThread().getName());
//
//                          rc.response().end(json.encode());
//                      }, throwable -> rc.response().setStatusCode(500));
//
//
//              //_vertx.<Stream<String>>executeBlocking(future -> {
//              //    try {
//              //        future.complete(_service.getData().get());
//              //    } catch (InterruptedException e) {
//              //        e.printStackTrace();
//              //    } catch (ExecutionException e) {
//              //        e.printStackTrace();
//              //    }
//              //}, result -> {
//              //    System.out.println("Is on event loop thread: " + Context.isOnEventLoopThread());
//              //    System.out.println("Is on vertx thread: " + Context.isOnVertxThread());
//              //    System.out.println("Is on worker context: " + Context.isOnWorkerThread());
//              //    System.out.println("Completed hello on thread: " + Thread.currentThread().getName());
//              //
//              //    rc.response().end(result.result().collect(JsonArray::new, JsonArray::add, JsonArray::addAll).encode());
//              //});
//          });
//      });

//
//JsonObject authConfig = new JsonObject().put("keyStore", new JsonObject()
//    .put("type", "jceks")
//    .put("path", "keystore.jceks")
//    .put("password", "secret"));
//
//JWTAuth authProvider = JWTAuth.create(vertx, authConfig);
//
//router.route("/*").handler(JWTAuthHandler.create(authProvider));

//router.route("/")
//      .produces("application/json")
//      .handler(rc -> {
//          System.out.println("auth request");
//          rc.response().putHeader("WWW-Authenticate", "asas");
//          rc.fail(401);
//      });