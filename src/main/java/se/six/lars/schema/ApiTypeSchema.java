package se.six.lars.schema;


import graphql.schema.*;
import se.six.lars.ApiUser;
import se.six.lars.IApiController;
import se.six.lars.types.*;

import java.security.spec.EncodedKeySpec;
import java.util.concurrent.CompletableFuture;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static se.six.lars.schema.ScalarTypes.*;

public class ApiTypeSchema
{

    private final GraphQLObjectType _sectorType = newObject()
        .name("Sector")
        .field(newFieldDefinition()
                   .type(GraphQLString)
                   .name("code")
                   .dataFetcher(env -> {
                       Sector sector = (Sector)env.getSource();
                       return CompletableFuture.completedFuture(sector.code());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLString)
                   .name("name")
                   .dataFetcher(env -> {
                       Sector sector = (Sector)env.getSource();
                       return CompletableFuture.completedFuture(sector.description());
                   }))
        .build();


    private final GraphQLObjectType _quotesType = newObject()
        .name("Quotes")
        .field(newFieldDefinition()
                   .type(ScalarTypes.GraphQLDate)
                   .name("lastUpdated")
                   .dataFetcher(env -> {
                       Quotes listing = (Quotes)env.getSource();
                       return CompletableFuture.completedFuture(listing.lastUpdated());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("openPrice")
                   .dataFetcher(env -> {
                       Quotes quotes = (Quotes)env.getSource();
                       return CompletableFuture.completedFuture(quotes.openPrice().orElse(Double.NaN));
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("lastPrice")
                   .dataFetcher(env -> {
                       Quotes quotes = (Quotes)env.getSource();
                       return CompletableFuture.completedFuture(quotes.lastPrice().orElse(Double.NaN));
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("askPrice")
                   .dataFetcher(env -> {
                       Quotes quotes = (Quotes)env.getSource();
                       return CompletableFuture.completedFuture(quotes.askPrice().orElse(Double.NaN));
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("bidPrice")
                   .dataFetcher(env -> {
                       Quotes quotes = (Quotes)env.getSource();
                       return CompletableFuture.completedFuture(quotes.bidPrice().orElse(Double.NaN));
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("highPrice")
                   .dataFetcher(env -> {
                       Quotes quotes = (Quotes)env.getSource();
                       return CompletableFuture.completedFuture(quotes.highPrice().orElse(Double.NaN));
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("lowPrice")
                   .dataFetcher(env -> {
                       Quotes quotes = (Quotes)env.getSource();
                       return CompletableFuture.completedFuture(quotes.lowPrice().orElse(Double.NaN));
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("tradedVolume")
                   .dataFetcher(env -> {
                       Quotes quotes = (Quotes)env.getSource();
                       return CompletableFuture.completedFuture(quotes.tradedVolume().orElse(Double.NaN));
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("tradedAmount")
                   .dataFetcher(env -> {
                       Quotes quotes = (Quotes)env.getSource();
                       return CompletableFuture.completedFuture(quotes.tradedAmount().orElse(Double.NaN));
                   }))
        .build();


    private final GraphQLObjectType _orderLevelType = newObject()
        .name("OrderLevel")
        .field(newFieldDefinition()
                   .type(GraphQLInt)
                   .name("level")
                   .dataFetcher(env -> {
                       OrderLevel level = (OrderLevel)env.getSource();
                       return CompletableFuture.completedFuture(level.level());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("askPrice")
                   .dataFetcher(env -> {
                       OrderLevel level = (OrderLevel)env.getSource();
                       return CompletableFuture.completedFuture(level.askPrice());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("askVolume")
                   .dataFetcher(env -> {
                       OrderLevel level = (OrderLevel)env.getSource();
                       return CompletableFuture.completedFuture(level.askVolume());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLInt)
                   .name("askOrders")
                   .dataFetcher(env -> {
                       OrderLevel level = (OrderLevel)env.getSource();
                       return CompletableFuture.completedFuture(level.askOrders());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("bidPrice")
                   .dataFetcher(env -> {
                       OrderLevel level = (OrderLevel)env.getSource();
                       return CompletableFuture.completedFuture(level.bidPrice());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLFloat)
                   .name("bidVolume")
                   .dataFetcher(env -> {
                       OrderLevel level = (OrderLevel)env.getSource();
                       return CompletableFuture.completedFuture(level.bidVolume());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLInt)
                   .name("bidOrders")
                   .dataFetcher(env -> {
                       OrderLevel level = (OrderLevel)env.getSource();
                       return CompletableFuture.completedFuture(level.bidOrders());
                   }))
        .build();

    private final GraphQLObjectType _orderBookType = newObject()
        .name("OrderBook")
        .field(newFieldDefinition()
                   .type(GraphQLDate)
                   .name("lastUpdated")
                   .dataFetcher(env -> {
                       OrderBook orderBook = (OrderBook)env.getSource();
                       return CompletableFuture.completedFuture(orderBook.lastUpdated());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLString)
                   .name("state")
                   .dataFetcher(env -> {
                       OrderBook orderBook = (OrderBook)env.getSource();
                       return CompletableFuture.completedFuture(orderBook.state());
                   }))
        .field(newFieldDefinition()
                   .type(new GraphQLList(_orderLevelType))
                   .name("levels")
                   .dataFetcher(env -> {
                       OrderBook orderBook = (OrderBook)env.getSource();
                       return CompletableFuture.completedFuture(orderBook.levels());
                   }))
        .build();

    private final GraphQLObjectType _organizationType = newObject()
        .name("Organization")
        .field(newFieldDefinition()
                   .type(new GraphQLNonNull(GraphQLString))
                   .name("id")
                   .dataFetcher(env -> {
                       Organization organization = (Organization)env.getSource();
                       return CompletableFuture.completedFuture(organization.id());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLString)
                   .name("name")
                   .dataFetcher(env -> {
                       Organization organization = (Organization)env.getSource();
                       return CompletableFuture.completedFuture(organization.name().orElse(null));
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLString)
                   .name("countryCode")
                   .dataFetcher(env -> {
                       Organization organization = (Organization)env.getSource();
                       return CompletableFuture.completedFuture(organization.countryCode().orElse(null));
                   }))
        .field(newFieldDefinition()
                   .type(_sectorType)
                   .name("industryClassification")
                   .dataFetcher(env -> {
                       Organization organization = (Organization)env.getSource();
                       return CompletableFuture.completedFuture(organization.industryClassification().orElse(null));
                   }))
        .field(newFieldDefinition()
                   .type(_sectorType)
                   .name("subIndustryClassification")
                   .dataFetcher(env -> {
                       Organization organization = (Organization)env.getSource();
                       return CompletableFuture.completedFuture(organization.subIndustryClassification().orElse(null));
                   }))
        .field(GraphQLFieldDefinition.<Listing>newFieldDefinition()
                   .type(new GraphQLTypeReference("Listing"))
                   .name("mostLiquidEquity")
                   .dataFetcher(env -> {
                       Organization organization = (Organization)env.getSource();
                       if (organization.mostLiquidEquity().isPresent()) {
                           IApiController apiController = (IApiController)env.getContext();
                           return apiController.listing(organization.mostLiquidEquity().get().id(), (ApiUser)env.getContext());
                       }
                       return CompletableFuture.completedFuture(null);
                   }))
        .build();

    private final GraphQLObjectType _listingType = newObject()
        .name("Listing")
        .field(newFieldDefinition()
                   .type(new GraphQLNonNull(GraphQLString))
                   .name("id")
                   .dataFetcher(env -> {
                       Listing listing = (Listing)env.getSource();
                       return CompletableFuture.completedFuture(listing.id());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLString)
                   .name("name")
                   .dataFetcher(env -> {
                       Listing listing = (Listing)env.getSource();
                       return CompletableFuture.completedFuture(listing.name());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLString)
                   .name("longName")
                   .dataFetcher(env -> {
                       Listing listing = (Listing)env.getSource();
                       return CompletableFuture.completedFuture(listing.longName());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLString)
                   .name("currencyCode")
                   .dataFetcher(env -> {
                       Listing listing = (Listing)env.getSource();
                       return CompletableFuture.completedFuture(listing.currencyCode());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLString)
                   .name("type")
                   .dataFetcher(env -> {
                       Listing listing = (Listing)env.getSource();
                       return CompletableFuture.completedFuture(listing.type());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLInt)
                   .name("roundLot")
                   .dataFetcher(env -> {
                       Listing listing = (Listing)env.getSource();
                       return CompletableFuture.completedFuture(listing.roundLot());
                   }))
        .field(newFieldDefinition()
                   .type(GraphQLDate)
                   .name("listingDate")
                   .dataFetcher(env -> {
                       Listing listing = (Listing)env.getSource();
                       return CompletableFuture.completedFuture(listing.listingDate());
                   }))
        .field(GraphQLFieldDefinition.<Quotes>newFieldDefinition()
                   .type(_quotesType)
                   .name("quotes")
                   .dataFetcher(env -> {
                       Listing listing = (Listing)env.getSource();
                       ApiRequestContext context = (ApiRequestContext)env.getContext();
                       return context.getApiController().listingQuotes(listing.id(), context.getUser());
                   }))
        .field(GraphQLFieldDefinition.<OrderBook>newFieldDefinition()
                   .type(_orderBookType)
                   .name("orderbook")
                   .dataFetcher(env -> {
                       Listing listing = (Listing)env.getSource();
                       ApiRequestContext context = (ApiRequestContext)env.getContext();
                       return context.getApiController().listingOrderBook(listing.id(), context.getUser());
                   }))
        .field(GraphQLFieldDefinition.<Organization>newFieldDefinition()
                   .type(_organizationType)
                   .name("issuer")
                   .dataFetcher(env -> {
                       Listing listing = (Listing)env.getSource();
                       ApiRequestContext context = (ApiRequestContext)env.getContext();

                       return null;//context.getApiController().organization(listing.getIssuer().id(), context.getUser());
                   }))
        .build();

    public GraphQLFieldDefinition.Builder<Listing> listingQuery()
    {
        return GraphQLFieldDefinition.<Listing>newFieldDefinition()
            .type(_listingType)
            .name("listing")
            .argument(newArgument()
                          .name("id")
                          .description("Listing identifier")
                          .type(new GraphQLNonNull(GraphQLString)))
            .dataFetcher(env -> {
                ApiRequestContext context = (ApiRequestContext)env.getContext();
                return context.getApiController().listing(env.getArgument("id"), context.getUser());
            });
    }

    public GraphQLFieldDefinition.Builder<Organization> organizationQuery()
    {
        return GraphQLFieldDefinition.<Organization>newFieldDefinition()
            .type(_organizationType)
            .name("organization")
            .argument(newArgument()
                          .name("id")
                          .description("Organization identifier")
                          .type(new GraphQLNonNull(GraphQLString)))
            .dataFetcher(env -> {
                ApiRequestContext context = (ApiRequestContext)env.getContext();
                return context.getApiController().organization(env.getArgument("id"), context.getUser());
            });
    }

}
