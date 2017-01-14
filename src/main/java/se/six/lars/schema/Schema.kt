package se.six.lars.schema

import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.relay.Connection
import graphql.relay.Relay
import graphql.relay.SimpleListConnection
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLTypeReference
import se.six.lars.types.*
import java.util.*
import java.util.concurrent.CompletableFuture

private val relay = Relay()
/**
 * Defines the Sector QL type
 */
private val sectorType = graphqlType("Sector") {
    field<String>("code") {
        description = "Sector code"
        dataFetcher = { env ->
            val sector = env.source as Sector
            succeeded(sector.code())
        }
    }
    field<String>("name") {
        dataFetcher = { env ->
            val sector = env.source as Sector
            succeeded(sector.description())
        }
    }
}

/**
 * Defines the Quotes QL type
 */
private val quotesType = graphqlType("Quotes") {
    field<Date>("lastUpdated") {
        dataFetcher = { env ->
            val listing = env.source as Quotes
            succeededOptional(listing.lastUpdated())
        }
    }
    field<Double>("openPrice") {
        dataFetcher = { env ->
            val listing = env.source as Quotes
            succeeded(listing.openPrice().orElse(Double.NaN))
        }
    }
    field<Double>("lowPrice") {
        dataFetcher = { env ->
            val listing = env.source as Quotes
            succeeded(listing.lowPrice().orElse(Double.NaN))
        }
    }
    field<Double>("lastPrice") {
        dataFetcher = { env ->
            val listing = env.source as Quotes
            succeeded(listing.lastPrice().orElse(Double.NaN))
        }
    }
    field<Double>("askPrice") {
        dataFetcher = { env ->
            val listing = env.source as Quotes
            succeeded(listing.askPrice().orElse(Double.NaN))
        }
    }
    field<Double>("bidPrice") {
        dataFetcher = { env ->
            val listing = env.source as Quotes
            succeeded(listing.bidPrice().orElse(Double.NaN))
        }
    }
    field<Double>("highPrice") {
        dataFetcher = { env ->
            val listing = env.source as Quotes
            succeeded(listing.highPrice().orElse(Double.NaN))
        }
    }
    field<Double>("tradedVolume") {
        dataFetcher = { env ->
            val listing = env.source as Quotes
            succeeded(listing.tradedVolume().orElse(Double.NaN))
        }
    }
    field<Double>("tradedAmount") {
        dataFetcher = { env ->
            val listing = env.source as Quotes
            succeeded(listing.tradedAmount().orElse(Double.NaN))
        }
    }
}

private val orderLevelType = graphqlType("OrderLevel") {
    field<Int>("level") {
        dataFetcher = { env ->
            val orderLevel = env.source as OrderLevel
            succeeded(orderLevel.level())
        }
    }
    field<Double>("askPrice") {
        dataFetcher = { env ->
            val orderLevel = env.source as OrderLevel
            succeeded(orderLevel.askPrice())
        }
    }
    field<Double>("askVolume") {
        dataFetcher = { env ->
            val orderLevel = env.source as OrderLevel
            succeeded(orderLevel.askVolume())
        }
    }
    field<Int>("askOrders") {
        dataFetcher = { env ->
            val orderLevel = env.source as OrderLevel
            succeeded(orderLevel.askOrders().toInt())
        }
    }
    field<Double>("bidPrice") {
        dataFetcher = { env ->
            val orderLevel = env.source as OrderLevel
            CompletableFuture.completedFuture<Double>(orderLevel.bidPrice())
        }
    }
    field<Double>("bidVolume") {
        dataFetcher = { env ->
            val orderLevel = env.source as OrderLevel
            succeeded(orderLevel.bidVolume())
        }
    }
    field<Int>("bidOrders") {
        dataFetcher = { env ->
            val orderLevel = env.source as OrderLevel
            succeeded(orderLevel.bidOrders().toInt())
        }
    }
}

private val orderBookType = graphqlType("OrderBook") {
    field<Date>("lastUpdated") {
        dataFetcher = { env ->
            val orderBook = env.source as OrderBook
            succeeded(orderBook.lastUpdated())
        }
    }
    field<String>("state") {
        dataFetcher = { env ->
            val orderBook = env.source as OrderBook
            succeeded(orderBook.state())
        }
    }
    field<List<OrderLevel>>("levels") {
        type = graphqlList(orderLevelType)
        dataFetcher = { env ->
            val orderBook = env.source as OrderBook
            succeeded(orderBook.levels())
        }
    }
}

private val organizationType = graphqlType("Organization") {
    field<String>("id") {
        type = graphqlNonNull(GraphQLString)
        dataFetcher = { env ->
            val org = env.source as Organization
            succeeded(org.id())
        }
    }
    field<String>("name") {
        dataFetcher = { env ->
            val org = env.source as Organization
            succeeded(org.name().orElse(null))
        }
    }
    field<String>("countryCode") {
        dataFetcher = { env ->
            val org = env.source as Organization
            succeeded(org.countryCode().orElse(null))
        }
    }
    field<Sector>("industryClassification") {
        type = sectorType
        dataFetcher = { env ->
            val org = env.source as Organization
            succeeded(org.industryClassification().orElse(null))
        }
    }
    field<Sector>("subIndustryClassification") {
        type = sectorType
        dataFetcher = { env ->
            val org = env.source as Organization
            succeeded(org.industryClassification().orElse(null))
        }
    }
    field<Listing>("mostLiquidEquity") {
        type = GraphQLTypeReference("Listing") // required as we have circular references
        dataFetcher = { env ->
            val org = env.source as Organization
            if (org.mostLiquidEquity().isPresent) {
                val context = env.context as ApiRequestContext
                context.apiController.listing(org.mostLiquidEquity().get().id(), context.user)
            } else
                succeeded(null)
        }
    }

}


private val listingType = graphqlType("Listing") {
    field<String>("id") {
        type = graphqlNonNull(GraphQLString)
        dataFetcher = { env ->
            val listing = env.source as Listing
            succeeded(listing.id())
        }
    }
    field<String>("name") {
        dataFetcher = { env ->
            val listing = env.source as Listing
            succeededOptional(listing.name())
        }
    }
    field<String>("longName") {
        dataFetcher = { env ->
            val listing = env.source as Listing
            succeededOptional(listing.longName())
        }
    }
    field<String>("currencyCode") {
        dataFetcher = { env ->
            val listing = env.source as Listing
            succeededOptional(listing.currencyCode())
        }
    }
    field<String>("type") {
        dataFetcher = { env ->
            val listing = env.source as Listing
            succeededOptional(listing.type())
        }
    }
    field<Int>("roundLot") {
        dataFetcher = { env ->
            val listing = env.source as Listing
            succeededOptionalInt(listing.roundLot())
        }
    }
    field<Date>("listingDate") {
        dataFetcher = { env ->
            val listing = env.source as Listing
            succeededOptional(listing.listingDate())
        }
    }
    field<Quotes>("quotes") {
        type = quotesType
        dataFetcher = { env ->
            val listing = env.source as Listing
            val context = env.context as ApiRequestContext
            context.apiController.listingQuotes(listing.id(), context.user)
        }
    }
    field<OrderBook>("orderbook") {
        type = orderBookType
        dataFetcher = { env ->
            val listing = env.source as Listing
            val context = env.context as ApiRequestContext
            context.apiController.listingOrderBook(listing.id(), context.user)
        }
    }
    field<Organization>("issuer") {
        type = organizationType
        dataFetcher = { env ->
            val listing = env.source as Listing
            val context = env.context as ApiRequestContext
            if (listing.issuer.isPresent)
                context.apiController.organization(listing.issuer.get().id(), context.user)
            else
                succeeded(null)
        }
    }
}

private val searchItemType = graphqlType("SearchItem") {
    field<String>("id") {
        dataFetcher = { env ->
            val item = env.source as SearchItem
            succeeded(item.id)
        }
    }
    field<Float>("score") {
        dataFetcher = { env ->
            val item = env.source as SearchItem
            succeeded(item.score)
        }
    }
    field<String>("name") {
        dataFetcher = { env ->
            val item = env.source as SearchItem
            succeeded(item.name)
        }
    }
    field<String>("longName") {
        dataFetcher = { env ->
            val item = env.source as SearchItem
            succeeded(item.longName)
        }
    }
    field<Listing>("listing") {
        type = listingType
        dataFetcher = { env ->
            val item = env.source as SearchItem
            val context = env.context as ApiRequestContext
            context.apiController.listing(item.id, context.user)
        }
    }
}

private val personType = graphqlType("Person") {
    field<Int>("socialSecurityId") {
        dataFetcher = { env ->
            val p = env.source as Person
            succeeded(p.id)
        }

    }
    field<String>("firstName") {
        dataFetcher = { env ->
            val p = env.source as Person
            succeeded(p.firstName)
        }

    }
    field<String>("lastName") {
        dataFetcher = { env ->
            val p = env.source as Person
            succeeded(p.lastName)
        }

    }
}

// Queries
private val listingQuery = graphqlField<Listing>("listing") {
    type = listingType
    argument<String>("id") {
        description = "Listing identifier"
        type = graphqlNonNull(GraphQLString)
    }
    dataFetcher = { env ->
        val context = env.context as ApiRequestContext
        context.apiController.listing(env.getArgument("id"), context.user)
    }
}

private val organizationQuery = graphqlField<Organization>("organization") {
    type = organizationType
    argument<String>("id") {
        description = "Organization identifier"
        type = graphqlNonNull(GraphQLString)
    }
    dataFetcher = { env ->
        val context = env.context as ApiRequestContext
        context.apiController.organization(env.getArgument("id"), context.user)
    }
}

private val personsQuery = graphqlField<List<Person>>("persons") {
    type = graphqlList(personType)
    dataFetcher = {
        succeeded(PersonRepository.allPersons())
    }
}

private val listingSearchQuery = graphqlField<List<SearchItem>>("listingSearch") {
    type = graphqlList(searchItemType)
    argument<String>("searchQuery") {
        type = graphqlNonNull(GraphQLString)
    }
    dataFetcher = { env ->
        val context = env.context as ApiRequestContext
        context.searchController.searchListings(env.getArgument("searchQuery"), context.user)
        //context.searchController.searchListings("abb", context.user)
    }
}

//var nodeInterface = relay.nodeInterface {
//    val resolvedGlobalId = relay.fromGlobalId(it as String)
//    //TODO: implement
//    null
//}

var searchEdgeType = relay.edgeType("SearchItem", searchItemType, null, listOf())
var searchConnectionType = relay.connectionType("SearchItem", searchEdgeType, listOf())

private val listingSearchQueryPaged = graphqlField<Connection<SearchItem>>("listingSearchPaged") {
    type = searchConnectionType
    argument<String>("searchQuery") {
        type = graphqlNonNull(GraphQLString)
    }
    argument<Int>("first")
    argument<Int>("last")
    argument<String>("before")
    argument<String>("after")
    dataFetcher = { env ->
        with(env.context as ApiRequestContext) {
            searchController
                    .searchListings(env.getArgument("searchQuery"), user)
                    .thenApply { searchResult ->
                        SimpleListConnection(searchResult).get(env)
                    }
        }
    }
}


// Mutations
private val personInput = GraphQLInputObjectType.newInputObject()
        .name("PersonInput")
        .field(GraphQLInputObjectField.newInputObjectField()
                       .name("socialSecurityId")
                       .type(graphqlNonNull(GraphQLInt)))
        .field(GraphQLInputObjectField.newInputObjectField()
                       .name("firstName")
                       .type(graphqlNonNull(GraphQLString)))
        .field(GraphQLInputObjectField.newInputObjectField()
                       .name("lastName")
                       .type(graphqlNonNull(GraphQLString)))
        .build()

private val addPersonMutation = graphqlField<Person>("addPerson") {
    type = personType // output type!
    argument<Person>("person") {
        type = graphqlNonNull(personInput)
    }
    dataFetcher = { env ->
        val person = env.getArgument<Map<String, Any>>("person")
        val id: Int = person["socialSecurityId"] as Int
        val fn: String = person["firstName"] as String
        val ln: String = person["lastName"] as String
        succeeded(PersonRepository.addPerson(id, fn, ln))
    }
}

private val removePersonMutation = graphqlField<Person>("removePerson") {
    type = personType // output type!
    argument<Int>("socialSecurityId") {
        type = graphqlNonNull(GraphQLInt)
    }
    dataFetcher = { env ->
        val id: Int = env.getArgument<Int>("socialSecurityId")
        succeeded(PersonRepository.removePerson(id))
    }
}

// Schema
val schema = graphqlSchema {
    queryType = graphqlType("QueryType") {
        field(listingQuery)
        field(organizationQuery)
        field(personsQuery)
        field(listingSearchQuery)
        field(listingSearchQueryPaged)
    }
    mutationType = graphqlType("MutationType") {
        field(addPersonMutation)
        field(removePersonMutation)
    }

}
