package se.lars.schema

import graphql.GraphQLInt
import graphql.GraphQLString
import graphql.relay.*
import graphql.schema.*
import se.lars.kutil.sendWithReply
import se.lars.kutil.succeeded
import se.lars.messages.SearchQuery
import se.lars.messages.SearchResult
import se.lars.types.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Defines the Sector QL type
 */
private val sectorType = newObject {
    name = "Sector"
    field<String> {
        name = "code"
        description = "Sector code"
        fetcher = { env ->
            val sector = env.source<Sector>()
            succeeded(sector.code)
        }
    }
    field<String> {
        name = "name"
        fetcher = { env ->
            val sector = env.source<Sector>()
            succeeded(sector.description)
        }
    }
}

/**
 * Defines the Quotes QL type
 */
private val quotesType = newObject {
    name = "Quotes"
    field<Date> {
        name = "lastUpdated"
        fetcher = { env ->
            succeeded(env.source<Quotes>().lastUpdated)
        }
    }
    field<Double> {
        name = "openPrice"
        fetcher = { env ->
            succeeded(env.source<Quotes>().openPrice ?: Double.NaN)
        }
    }
    field<Double> {
        name = "lowPrice"
        fetcher = { env ->
            succeeded(env.source<Quotes>().lowPrice ?: Double.NaN)
        }
    }
    field<Double> {
        name = "lastPrice"
        fetcher = { env ->
            succeeded(env.source<Quotes>().lastPrice ?: Double.NaN)
        }
    }
    field<Double> {
        name = "askPrice"
        fetcher = { env ->
            succeeded(env.source<Quotes>().askPrice ?: Double.NaN)
        }
    }
    field<Double> {
        name = "bidPrice"
        fetcher = { env ->
            succeeded(env.source<Quotes>().bidPrice ?: Double.NaN)
        }
    }
    field<Double> {
        name = "highPrice"
        fetcher = { env ->
            succeeded(env.source<Quotes>().highPrice ?: Double.NaN)
        }
    }
    field<Double> {
        name = "tradedVolume"
        fetcher = { env ->
            succeeded(env.source<Quotes>().tradedVolume ?: Double.NaN)
        }
    }
    field<Double> {
        name = "tradedAmount"
        fetcher = { env ->
            succeeded(env.source<Quotes>().tradedAmount ?: Double.NaN)
        }
    }
}

private val orderLevelType = newObject {
    name = "OrderLevel"
    field<Int> {
        name = "level"
        fetcher = { env ->
            succeeded(env.source<OrderLevel>().level)
        }
    }
    field<Double> {
        name = "askPrice"
        fetcher = { env ->
            succeeded(env.source<OrderLevel>().askPrice)
        }
    }
    field<Double> {
        name = "askVolume"
        fetcher = { env ->
            succeeded(env.source<OrderLevel>().askVolume)
        }
    }
    field<Int> {
        name = "askOrders"
        fetcher = { env ->
            succeeded(env.source<OrderLevel>().askOrders.toInt())
        }
    }
    field<Double> {
        name = "bidPrice"
        fetcher = { env ->
            succeeded(env.source<OrderLevel>().bidPrice)
        }
    }
    field<Double> {
        name = "bidVolume"
        fetcher = { env ->
            succeeded(env.source<OrderLevel>().bidVolume)
        }
    }
    field<Int> {
        name = "bidOrders"
        fetcher = { env ->
            succeeded(env.source<OrderLevel>().bidOrders.toInt())
        }
    }
}

private val orderBookType = newObject {
    name = "OrderBook"
    field<Date> {
        name = "lastUpdated"
        fetcher = { env ->
            succeeded(env.source<OrderBook>().lastUpdated)
        }
    }
    field<String> {
        name = "state"
        fetcher = { env ->
            succeeded(env.source<OrderBook>().state)
        }
    }
    field<List<OrderLevel>> {
        name = "levels"
        type = GraphQLList(orderLevelType)
        fetcher = { env ->
            succeeded(env.source<OrderBook>().levels)
        }
    }
}

private val organizationType = newObject {
    name = "Organization"
    field<String> {
        name = "id"
        type = GraphQLNonNull(GraphQLString)
        fetcher = { env ->
            succeeded(env.source<Organization>().id)
        }
    }
    field<String> {
        name = "name"
        fetcher = { env ->
            val org = env.source<Organization>()
            succeeded(org.name)
        }
    }
    field<String> {
        name = "countryCode"
        fetcher = { env ->
            succeeded(env.source<Organization>().countryCode)
        }
    }
    field<Sector> {
        name = "industryClassification"
        type = sectorType
        fetcher = { env ->
            succeeded(env.source<Organization>().industryClassification)
        }
    }
    field<Sector> {
        name = "subIndustryClassification"
        type = sectorType
        fetcher = { env ->
            succeeded(env.source<Organization>().subIndustryClassification)
        }
    }
    field<Listing> {
        name = "mostLiquidEquity"
        type = GraphQLTypeReference("Listing") // required as we have circular references
        fetcher = { env ->
            val org = env.source<Organization>()
            if (org.mostLiquidEquity != null) {
                val context = env.context<ApiRequestContext>()
                context.apiController.listing(org.mostLiquidEquity.id, context.user)
            } else
                succeeded(null)
        }
    }

}


private val listingType = newObject {
    name = "Listing"
    field<String> {
        name = "id"
        type = GraphQLNonNull(GraphQLString)
        fetcher = { env ->
            succeeded(env.source<Listing>().id)
        }
    }
    field<String> {
        name = "name"
        fetcher = { env ->
            succeeded(env.source<Listing>().name)
        }
    }
    field<String> {
        name = "longName"
        fetcher = { env ->
            succeeded(env.source<Listing>().longName)
        }
    }
    field<String> {
        name = "currencyCode"
        fetcher = { env ->
            succeeded(env.source<Listing>().currencyCode)
        }
    }
    field<String> {
        name = "type"
        fetcher = { env ->
            succeeded(env.source<Listing>().type)
        }
    }
    field<Int> {
        name = "roundLot"
        fetcher = { env ->
            succeeded(env.source<Listing>().roundLot)
        }
    }
    field<Date> {
        name = "listingDate"
        fetcher = { env ->
            succeeded(env.source<Listing>().listingDate)
        }
    }
    field<Quotes> {
        name = "quotes"
        type = quotesType
        fetcher = { env ->
            val listing = env.source<Listing>()
            val context = env.context<ApiRequestContext>()
            context.apiController.listingQuotes(listing.id, context.user)
        }
    }
    field<OrderBook> {
        name = "orderbook"
        type = orderBookType
        fetcher = { env ->
            val listing = env.source<Listing>()
            val context = env.context<ApiRequestContext>()
            context.apiController.listingOrderBook(listing.id, context.user)
        }
    }
    field<Organization> {
        name = "issuer"
        type = organizationType
        fetcher = { env ->
            val listing = env.source<Listing>()
            val context = env.context<ApiRequestContext>()
            if (listing.issuer != null)
                context.apiController.organization(listing.issuer.id, context.user)
            else
                succeeded(null)
        }
    }
}

private val searchItemType = newObject {
    name = "SearchItem"
    field<String> {
        name = "id"
        fetcher = { succeeded(it.source<SearchItem>().id) }
    }
    field<Float> {
        name = "score"
        fetcher = { succeeded(it.source<SearchItem>().score) }
    }
    field<String> {
        name = "name"
        fetcher = { succeeded(it.source<SearchItem>().name) }
    }
    field<String> {
        name = "longName"
        fetcher = { succeeded(it.source<SearchItem>().longName) }
    }
    field<Listing> {
        name = "listing"
        type = listingType
        fetcher = { env ->
            with(env.context<ApiRequestContext>()) {
                apiController.listing(env.source<SearchItem>().id, user)
            }
        }
    }
}

private val personType = newObject {
    name = "Person"
    field<Int> {
        name = "socialSecurityId"
        fetcher = { env ->
            succeeded(env.source<Person>().id)
        }

    }
    field<String> {
        name = "firstName"
        fetcher = { env ->
            succeeded(env.source<Person>().firstName)
        }

    }
    field<String> {
        name = "lastName"
        fetcher = { env ->
            succeeded(env.source<Person>().lastName)
        }
    }
}

// Queries
private val listingQuery = newField<Listing> {
    name = "listing"
    type = listingType
    argument {
        name = "id"
        description = "Listing identifier"
        type = GraphQLNonNull(GraphQLString)
    }
    fetcher = { env ->
        val context = env.context<ApiRequestContext>()
        context.apiController.listing(env.argument("id")!!, context.user)
    }
}

private val organizationQuery = newField<Organization> {
    name = "organization"
    type = organizationType
    argument {
        name = "id"
        description = "Organization identifier"
        type = GraphQLNonNull(GraphQLString)
    }
    fetcher = { env ->
        val context = env.context<ApiRequestContext>()
        context.apiController.organization(env.argument("id")!!, context.user)
    }
}

private val personsQuery = newField<List<Person>> {
    name = "persons"
    type = GraphQLList(personType)
    fetcher = { succeeded(PersonRepository.allPersons()) }
}

private val listingSearchQuery = newField<List<SearchItem>> {
    name = "listingSearch"
    type = GraphQLList(searchItemType)
    argument {
        name = "searchQuery"
        type = GraphQLNonNull(GraphQLString)
    }
    fetcher = { env ->
        env.context<ApiRequestContext>()
                .eventBus
                .sendWithReply<SearchQuery, SearchResult>(SearchQuery(env.argument("searchQuery")!!))
                .thenApply { it.result }
    }
}

private val listingSearchQueryPaged = newField<Connection<SearchItem>> {
    name = "listingSearchPaged"
    type = connectionType<SearchItem> {
        baseName = "Search"
        edgeType = edgeType<SearchItem> {
            baseName = "Search"
            nodeType = searchItemType
        }
    }
    argument {
        name = "searchQuery"
        type = GraphQLNonNull(GraphQLString)
    }
    arguments += connectionFieldArguments
    fetcher = { env ->
        with(env.context<ApiRequestContext>()) {
            searchController.searchListings(env.argument<String>("searchQuery")!!, user)
                    .thenApply { SimpleListConnection(it).fetch(env) }
        }
    }
}

// Mutations
private val personInputType = newInputObject {
    name = "PersonInput"
    field {
        name = "socialSecurityId"
        type = GraphQLNonNull(GraphQLInt)
    }
    field {
        name = "firstName"
        type = GraphQLNonNull(GraphQLString)
    }
    field {
        name = "lastName"
        type = GraphQLNonNull(GraphQLString)
    }
}

private val addPersonMutation = newField<Person> {
    name = "addPerson"
    type = personType // output type!
    argument {
        name = "person"
        type = GraphQLNonNull(personInputType)
    }
    fetcher = { env ->
        val person = env.argument<Map<String, Any>>("person")!!
        val id: Int = person["socialSecurityId"] as Int
        val fn: String = person["firstName"] as String
        val ln: String = person["lastName"] as String
        succeeded(PersonRepository.addPerson(id, fn, ln))
    }
}

private val removePersonMutation = newField<Person> {
    name = "removePerson"
    type = personType // output type!
    argument {
        name = "socialSecurityId"
        type = GraphQLNonNull(GraphQLInt)
    }
    fetcher = { env ->
        val id: Int = env.argument<Int>("socialSecurityId")!!
        succeeded(PersonRepository.removePerson(id))
    }
}

// Schema
val marketDataSchema = newSchema {
    query = newObject {
        name = "QueryType"
        fields += listingQuery
        fields += organizationQuery
        fields += personsQuery
        fields += listingSearchQuery
        fields += listingSearchQueryPaged
    }
    mutation = newObject {
        name = "MutationType"
        field(addPersonMutation)
        field(removePersonMutation)
    }
    subscription = newObject {
        name = "SubsciptionType"
        field<String> {
            name = "currentTime"
            fetcher {
                val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                succeeded(fmt.format(Date()))
            }
        }
    }

}
