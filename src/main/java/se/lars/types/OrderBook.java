package se.lars.types;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Date;
import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableOrderBook.class)
@JsonDeserialize(as = ImmutableOrderBook.class)
public interface OrderBook
{
    Date lastUpdated();
    String  state();
    List<OrderLevel> levels();
}
