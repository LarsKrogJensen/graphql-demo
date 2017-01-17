package se.lars.kutil

import io.vertx.core.Context
import io.vertx.core.Vertx
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

fun <T> CompletionStage<T>.thenOn(context: Context): CompletionStage<T> {
    val future = CompletableFuture<T>()
    whenComplete { result, error ->
        if (context == Vertx.currentContext()) {
            future.complete(result, error)
        } else {
            context.runOnContext {
                future.complete(result, error)
            }
        }
    }
    return future
}

fun <T> CompletableFuture<T>.complete(result: T?, error: Throwable?) {
    if (error != null)
        completeExceptionally(error)
    else
        complete(result)
}
