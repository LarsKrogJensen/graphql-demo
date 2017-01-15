package se.lars;

import io.vertx.core.json.JsonObject;
import rx.Observable;
import rx.Single;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface IMyService
{
    CompletableFuture<Stream<String>> getData();

    Observable<String> getData2();

    Single<JsonObject> getData3();
}
