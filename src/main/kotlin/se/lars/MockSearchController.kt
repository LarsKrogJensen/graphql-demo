package se.lars

import io.vertx.ext.auth.jwt.impl.JWTUser
import se.lars.types.SearchItem
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class MockSearchController : ISearchController {
    override fun searchListings(query: String, user: JWTUser): CompletionStage<List<SearchItem>> {
        return CompletableFuture.completedFuture(emptyList())
    }
}