package se.lars.messages

import se.lars.codec.KryoCodecAware
import se.lars.types.SearchItem

@KryoCodecAware
data class SearchQuery(val query: String)

@KryoCodecAware
data class SearchResult(val result: List<SearchItem>)
