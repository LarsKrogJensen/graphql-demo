package se.six.lars

import se.six.lars.types.SearchItem
import java.util.concurrent.CompletionStage


interface ISearchController {
    fun searchListings(query: String,
                       user: ApiUser): CompletionStage<List<SearchItem>>
}
