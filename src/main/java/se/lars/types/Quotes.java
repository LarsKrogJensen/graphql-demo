package se.lars.types;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.*;

@Value.Immutable
@JsonSerialize(as = ImmutableQuotes.class)
@JsonDeserialize(as = ImmutableQuotes.class)
public interface Quotes
{
    Optional<Date> lastUpdated();
    OptionalDouble openPrice();
    OptionalDouble lastPrice();
    OptionalDouble askPrice();
    OptionalDouble bidPrice();
    OptionalDouble highPrice();
    OptionalDouble lowPrice();
    OptionalDouble tradedVolume();
    OptionalDouble tradedAmount();
}
