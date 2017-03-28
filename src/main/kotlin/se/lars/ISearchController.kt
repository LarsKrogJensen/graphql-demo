package se.lars

import io.vertx.ext.auth.jwt.impl.JWTUser
import se.lars.auth.ApiUser
import se.lars.types.SearchItem
import java.util.concurrent.CompletionStage


interface ISearchController {
    fun searchListings(query: String, user: JWTUser): CompletionStage<List<SearchItem>>
}
