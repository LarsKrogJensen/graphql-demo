package se.lars

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.net.PemKeyCertOptions
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.JWTAuthHandler
import io.vertx.ext.web.handler.StaticHandler
import se.lars.accesslog.AccessLogHandler
import se.lars.auth.JWTAuthenticator
import se.lars.chat.ChatSystemHandler
import se.lars.kutil.loggerFor
import se.lars.kutil.resolveBool
import se.lars.kutil.resolveInt
import se.lars.kutil.router
import javax.inject.Inject
import javax.inject.Named


class WebServerVerticle
@Inject
constructor(
        @Named("config")
        private val _config: JsonObject,
        private val _graphQLHandler: GraphQLHandler,
        private val _graphQLHandlerWs: GraphQLHandlerOverWS,
        private val _chatHandler: ChatSystemHandler,
        private val _apiController: IApiController) : AbstractVerticle() {
    private val _log = loggerFor<WebServerVerticle>()

    override fun start(startFuture: Future<Void>) {
        val options = HttpServerOptions().apply {
            isCompressionSupported = true
            if (_config.resolveBool("http.useSsl") ?: false) {
                isUseAlpn = true
                isSsl = true
                pemKeyCertOptions = PemKeyCertOptions().apply {
                    keyPath = "tls/server-key.pem"
                    certPath = "tls/server-cert.pem"
                }
            }
        }

        // configure cross domain access
        val corsHandler = with(CorsHandler.create("*")) {
            allowCredentials(true)
            allowedMethod(HttpMethod.POST)
            allowedHeaders(setOf("content-type", "authorization"))
        }

        val keystoreConfig = JsonObject().put("keyStore", JsonObject()
                .put("path", "keystore.jceks")
                .put("type", "jceks")
                .put("password", "secret"))

        val provider = JWTAuth.create(vertx, keystoreConfig)

        val router = router(vertx) {
            route().handler(AccessLogHandler.create("%r %s \"%{Content-Type}o\" %D %T %B"))
            route().handler(corsHandler)
            route().handler(BodyHandler.create())
            //            route().handler(CookieHandler.create())
            //            route().handler(SessionHandler.create(LocalSessionStore.create(vertx)))
            //            route().handler(UserSessionHandler.create(_authProvider))
            route("/authenticate").handler(JWTAuthenticator(_apiController, provider))
            route("/graphql*").handler(JWTAuthHandler.create(provider))
            route("/chat").handler(_chatHandler)
            route("/graphql").handler(_graphQLHandler)
            route("/graphqlws").handler(_graphQLHandlerWs)


            route("/*").handler(StaticHandler.create().setCachingEnabled(false))
        }

        val port: Int = _config.resolveInt("http.port") ?: 8080

        vertx.createHttpServer(options)
                .requestHandler { router.accept(it) }
                .listen(port) {
                    when (it.succeeded()) {
                        true  -> {
                            _log.info("Http service started. on port " + it.result().actualPort())
                            startFuture.succeeded()
                        }
                        false -> {
                            _log.error("Http service failed to started.")
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