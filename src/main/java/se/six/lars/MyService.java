package se.six.lars;


import com.google.common.collect.Lists;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import rx.Observable;
import rx.Single;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class MyService
    implements IMyService
{

    private Vertx _vertx;
    private HttpClient _httpClient;

    @Inject
    public MyService(Vertx vertx)
    {
        _vertx = vertx;

        HttpClientOptions options = new HttpClientOptions()
            .setProtocolVersion(HttpVersion.HTTP_2)
            .setSsl(true)
            .setUseAlpn(true)
            .setTrustAll(true)
            .setDefaultHost("api.six.se")
            .setDefaultPort(443)
            .setLogActivity(true);

        _httpClient = _vertx.createHttpClient(options);
    }

    @Override
    public CompletableFuture<Stream<String>> getData()
    {
        return CompletableFuture.supplyAsync(() -> Stream.of("Lars", "Är", "Bäst"));
    }

    @Override
    public Observable<String> getData2()
    {
        return Observable.create(subscriber -> {
            CompletableFuture.runAsync(() -> {
                subscriber.onNext("Lars");
                subscriber.onNext("Är");
                subscriber.onNext("Bäst");
                subscriber.onCompleted();
            });
        });
    }

    @Override
    public Single<JsonObject> getData3()
    {
        return Single.create(singleSubscriber -> {
            _httpClient.request(HttpMethod.GET, "/v2/populations")
                       .putHeader("authorization", "Basic c2l4OnNpeHNpeHNpeA==")
                       .handler(response -> {
                           System.out.println("Service response, version: " + response.version());
                           if (response.statusCode() == 200) {
                               response.bodyHandler(buffer -> {
                                   singleSubscriber.onSuccess(buffer.toJsonObject());
                               });
                           } else {
                               System.out.println("Status code: " + response.statusCode());
                               singleSubscriber.onError(new Exception(response.statusMessage()));
                           }
                       })
                       .end();
        });
    }
}
